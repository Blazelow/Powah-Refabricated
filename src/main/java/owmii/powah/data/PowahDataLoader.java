package owmii.powah.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.api.FluidCoolantConfig;
import owmii.powah.api.MagmatorFuelValue;
import owmii.powah.api.PassiveHeatSourceConfig;
import owmii.powah.api.SolidCoolantConfig;
import owmii.powah.recipe.ReactorFuel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Replaces NeoForge DataMaps.
 * Loads JSON files from data/powah/powah_data/<type>/<id>.json on server data reload.
 */
public class PowahDataLoader implements SimpleResourceReloadListener<PowahDataLoader.LoadedData> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ResourceLocation ID = Powah.id("data_loader");

    private static Map<ResourceLocation, ReactorFuel> reactorFuels = new HashMap<>();
    private static Map<ResourceLocation, FluidCoolantConfig> fluidCoolants = new HashMap<>();
    private static Map<ResourceLocation, SolidCoolantConfig> solidCoolants = new HashMap<>();
    private static Map<ResourceLocation, MagmatorFuelValue> magmatorFuels = new HashMap<>();
    private static Map<ResourceLocation, PassiveHeatSourceConfig> blockHeatSources = new HashMap<>();
    private static Map<ResourceLocation, PassiveHeatSourceConfig> fluidHeatSources = new HashMap<>();

    public static void register() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new PowahDataLoader());
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public CompletableFuture<LoadedData> load(ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            var data = new LoadedData();
            loadMap(manager, "reactor_fuel", ReactorFuel.CODEC, data.reactorFuels);
            loadMap(manager, "fluid_coolant", FluidCoolantConfig.CODEC, data.fluidCoolants);
            loadMap(manager, "solid_coolant", SolidCoolantConfig.CODEC, data.solidCoolants);
            loadMap(manager, "magmator_fuel", MagmatorFuelValue.CODEC, data.magmatorFuels);
            loadMap(manager, "block_heat_source", PassiveHeatSourceConfig.CODEC, data.blockHeatSources);
            loadMap(manager, "fluid_heat_source", PassiveHeatSourceConfig.CODEC, data.fluidHeatSources);
            return data;
        }, executor);
    }

    @Override
    public CompletableFuture<Void> apply(LoadedData data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
        return CompletableFuture.runAsync(() -> {
            reactorFuels = data.reactorFuels;
            fluidCoolants = data.fluidCoolants;
            solidCoolants = data.solidCoolants;
            magmatorFuels = data.magmatorFuels;
            blockHeatSources = data.blockHeatSources;
            fluidHeatSources = data.fluidHeatSources;
        }, executor);
    }

    private static <T> void loadMap(ResourceManager manager, String type,
            com.mojang.serialization.Codec<T> codec, Map<ResourceLocation, T> out) {
        var prefix = "powah_data/" + type;
        manager.listResources(prefix, id -> id.getPath().endsWith(".json")).forEach((id, resource) -> {
            try (var reader = resource.openAsReader()) {
                JsonElement json = GsonHelper.fromJson(GSON, reader, JsonElement.class);
                var result = codec.parse(JsonOps.INSTANCE, json);
                result.result().ifPresent(value -> {
                    // Strip prefix and .json to get the registry key
                    String path = id.getPath();
                    path = path.substring(prefix.length() + 1, path.length() - 5);
                    out.put(ResourceLocation.fromNamespaceAndPath(id.getNamespace(), path), value);
                });
            } catch (Exception e) {
                Powah.LOGGER.error("Failed to load Powah data {}: {}", id, e.getMessage());
            }
        });
    }

    // --- Static accessors used by PowahAPI ---

    @Nullable
    public static ReactorFuel getReactorFuel(Item item) {
        var key = BuiltInRegistries.ITEM.getKey(item);
        return reactorFuels.get(key);
    }

    @Nullable
    public static FluidCoolantConfig getFluidCoolant(Fluid fluid) {
        var key = BuiltInRegistries.FLUID.getKey(fluid);
        return fluidCoolants.get(key);
    }

    @Nullable
    public static SolidCoolantConfig getSolidCoolant(ItemLike item) {
        var key = BuiltInRegistries.ITEM.getKey(item.asItem());
        return solidCoolants.get(key);
    }

    @Nullable
    public static MagmatorFuelValue getMagmatorFuel(Fluid fluid) {
        var key = BuiltInRegistries.FLUID.getKey(fluid);
        return magmatorFuels.get(key);
    }

    @Nullable
    public static PassiveHeatSourceConfig getBlockHeatSource(Block block) {
        var key = BuiltInRegistries.BLOCK.getKey(block);
        return blockHeatSources.get(key);
    }

    @Nullable
    public static PassiveHeatSourceConfig getFluidHeatSource(Fluid fluid) {
        var key = BuiltInRegistries.FLUID.getKey(fluid);
        return fluidHeatSources.get(key);
    }

    public static class LoadedData {
        Map<ResourceLocation, ReactorFuel> reactorFuels = new HashMap<>();
        Map<ResourceLocation, FluidCoolantConfig> fluidCoolants = new HashMap<>();
        Map<ResourceLocation, SolidCoolantConfig> solidCoolants = new HashMap<>();
        Map<ResourceLocation, MagmatorFuelValue> magmatorFuels = new HashMap<>();
        Map<ResourceLocation, PassiveHeatSourceConfig> blockHeatSources = new HashMap<>();
        Map<ResourceLocation, PassiveHeatSourceConfig> fluidHeatSources = new HashMap<>();
    }
}
