package owmii.powah.lib.datamap;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.TagKey;
import owmii.powah.Powah;

public final class DataMaps {
    private static final List<DataMapType<?, ?>> TYPES = new ArrayList<>();
    private static final Map<DataMapType<?, ?>, List<RawFile>> RAW = new HashMap<>();

    private record RawFile(boolean replace, Map<String, JsonElement> values) {
    }

    private DataMaps() {
    }

    public static void register(DataMapType<?, ?> type) {
        TYPES.add(type);
    }

    public static void init() {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloadListener(Powah.id("data_maps"), new Loader());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> resolveAll());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> resolveAll());
        PayloadTypeRegistry.clientboundPlay().register(SyncPayload.TYPE, SyncPayload.CODEC);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> ServerPlayNetworking.send(player, buildSync()));
    }

    public static void initClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncPayload.TYPE, (payload, context) -> applySync(payload));
    }

    private static final class Loader implements ResourceManagerReloadListener {
        @Override
        public void onResourceManagerReload(ResourceManager manager) {
            RAW.clear();
            for (var type : TYPES) {
                List<RawFile> files = new ArrayList<>();
                for (var resource : manager.getResourceStack(Powah.id(type.path()))) {
                    try (var reader = resource.openAsReader()) {
                        JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                        boolean replace = json.has("replace") && json.get("replace").getAsBoolean();
                        Map<String, JsonElement> values = new LinkedHashMap<>();
                        if (json.has("values")) {
                            json.getAsJsonObject("values").entrySet().forEach(e -> values.put(e.getKey(), e.getValue()));
                        }
                        files.add(new RawFile(replace, values));
                    } catch (IOException | RuntimeException e) {
                        Powah.LOGGER.error("Failed to read data map {}", type.id(), e);
                    }
                }
                RAW.put(type, files);
            }
        }
    }

    private static void resolveAll() {
        for (var type : TYPES) {
            resolve(type);
        }
    }

    @SuppressWarnings("unchecked")
    private static <R, T> void resolve(DataMapType<R, T> type) {
        Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.getValue(type.registry().identifier());
        Map<ResourceKey<R>, T> result = new LinkedHashMap<>();
        for (var file : RAW.getOrDefault(type, List.of())) {
            if (file.replace()) {
                result.clear();
            }
            file.values().forEach((key, json) -> {
                var value = type.codec().parse(JsonOps.INSTANCE, json).resultOrPartial(err -> Powah.LOGGER.error("Bad data map entry {}: {}", key, err));
                if (value.isEmpty()) {
                    return;
                }
                if (key.startsWith("#")) {
                    var tagId = Identifier.tryParse(key.substring(1));
                    if (tagId != null) {
                        registry.getTagOrEmpty(TagKey.create(type.registry(), tagId))
                                .forEach(holder -> holder.unwrapKey().ifPresent(k -> result.put(k, value.get())));
                    }
                } else {
                    var id = Identifier.tryParse(key);
                    if (id != null) {
                        result.put(ResourceKey.create(type.registry(), id), value.get());
                    }
                }
            });
        }
        type.setValues(result);
    }

    private static SyncPayload buildSync() {
        HashMap<Identifier, HashMap<Identifier, Tag>> data = new HashMap<>();
        for (var type : TYPES) {
            data.put(type.registry().identifier().withSuffix("/" + type.id().getPath()), encode(type));
        }
        return new SyncPayload(data);
    }

    private static <R, T> HashMap<Identifier, Tag> encode(DataMapType<R, T> type) {
        HashMap<Identifier, Tag> entries = new HashMap<>();
        type.all().forEach((key, value) -> type.codec().encodeStart(NbtOps.INSTANCE, value).result()
                .ifPresent(tag -> entries.put(key.identifier(), tag)));
        return entries;
    }

    private static void applySync(SyncPayload payload) {
        for (var type : TYPES) {
            applyType(type, payload.data().get(type.registry().identifier().withSuffix("/" + type.id().getPath())));
        }
    }

    private static <R, T> void applyType(DataMapType<R, T> type, Map<Identifier, Tag> entries) {
        if (entries == null) {
            return;
        }
        Map<ResourceKey<R>, T> result = new HashMap<>();
        entries.forEach((id, tag) -> type.codec().parse(NbtOps.INSTANCE, tag).result()
                .ifPresent(value -> result.put(ResourceKey.create(type.registry(), id), value)));
        type.setValues(result);
    }

    public record SyncPayload(HashMap<Identifier, HashMap<Identifier, Tag>> data) implements CustomPacketPayload {
        public static final Type<SyncPayload> TYPE = new Type<>(Powah.id("data_map_sync"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncPayload> CODEC = ByteBufCodecs
                .map(HashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.map(HashMap::new, Identifier.STREAM_CODEC, ByteBufCodecs.TAG))
                .map(SyncPayload::new, SyncPayload::data)
                .cast();

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
