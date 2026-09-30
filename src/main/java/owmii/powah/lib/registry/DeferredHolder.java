package owmii.powah.lib.registry;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DeferredHolder<T, I extends T> implements Supplier<I> {
    private final ResourceKey<? extends net.minecraft.core.Registry<T>> registryKey;
    private final ResourceLocation id;
    private I value;

    DeferredHolder(ResourceKey<? extends net.minecraft.core.Registry<T>> registryKey, ResourceLocation id) {
        this.registryKey = registryKey;
        this.id = id;
    }

    void bind(I value) {
        this.value = value;
    }

    public ResourceLocation getId() {
        return id;
    }

    public ResourceKey<T> getKey() {
        return ResourceKey.create(registryKey, id);
    }

    public boolean isBound() {
        return value != null;
    }

    public I value() {
        return get();
    }

    @Override
    public I get() {
        if (value == null) {
            throw new NullPointerException("Trying to access unbound value: " + getKey());
        }
        return value;
    }
}
