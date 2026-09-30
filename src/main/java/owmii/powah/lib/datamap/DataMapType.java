package owmii.powah.lib.datamap;

import com.mojang.serialization.Codec;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

public final class DataMapType<R, T> {
    private final Identifier id;
    private final ResourceKey<? extends Registry<R>> registry;
    private final Codec<T> codec;
    private volatile Map<ResourceKey<R>, T> values = Map.of();

    private DataMapType(Identifier id, ResourceKey<? extends Registry<R>> registry, Codec<T> codec) {
        this.id = id;
        this.registry = registry;
        this.codec = codec;
    }

    public static <R, T> DataMapType<R, T> create(Identifier id, ResourceKey<? extends Registry<R>> registry, Codec<T> codec) {
        return new DataMapType<>(id, registry, codec);
    }

    public Identifier id() {
        return id;
    }

    public ResourceKey<? extends Registry<R>> registry() {
        return registry;
    }

    public Codec<T> codec() {
        return codec;
    }

    @Nullable
    public T get(ResourceKey<R> key) {
        return values.get(key);
    }

    public Map<ResourceKey<R>, T> all() {
        return values;
    }

    void setValues(Map<ResourceKey<R>, T> values) {
        this.values = Map.copyOf(values);
    }

    String path() {
        return "data_maps/" + registry.identifier().getPath() + "/" + id.getPath() + ".json";
    }
}
