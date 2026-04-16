package owmii.powah.lib.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public class VarReg<V extends Enum<V> & IVariant<V>, E> {
    private static final Map<String, List<String>> ALL_VARIANTS = new HashMap<>();

    public static List<String> getSiblingIds(String name) {
        return ALL_VARIANTS.getOrDefault(name, List.of(name));
    }

    private final LinkedHashMap<V, E> all = new LinkedHashMap<>();

    public <R extends E> VarReg(Registry<R> registry, String modId, String name, VariantConstructor<V, R> ctor, V[] variants) {
        for (V variant : variants) {
            var entryName = name + "_" + variant.getName();
            ALL_VARIANTS.computeIfAbsent(name, s -> new ArrayList<>()).add(entryName);
            var registered = Registry.register(registry, ResourceLocation.fromNamespaceAndPath(modId, entryName), ctor.get(variant));
            this.all.put(variant, registered);
        }
    }

    public E[] getArr(IntFunction<E[]> generator) {
        return getAll().stream().toArray(generator);
    }

    public List<E> getAll() {
        return List.copyOf(all.values());
    }

    public E get(V variant) {
        return this.all.get(variant);
    }

    @FunctionalInterface
    public interface VariantConstructor<V extends Enum<V> & IVariant<V>, E> {
        E get(V variant);
    }
}
