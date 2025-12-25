package owmii.powah.lib.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import owmii.powah.block.Tier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TieredItemReg {
    private static final Map<String, List<String>> ALL_VARIANTS = new HashMap<>();

    public static List<String> getSiblingIds(String name) {
        return ALL_VARIANTS.getOrDefault(name, List.of(name));
    }

    private final LinkedHashMap<Tier, Supplier<Item>> all = new LinkedHashMap<>();

    public TieredItemReg(DeferredRegister.Items dr, String name, Factory factory, Tier[] variants) {
        for (Tier variant : variants) {
            var entryName = name + "_" + variant.getName();
            ALL_VARIANTS.computeIfAbsent(name, s -> new ArrayList<>()).add(entryName);
            this.all.put(variant, dr.registerItem(entryName, props -> factory.get(variant, props)));
        }
    }

    public Item[] getArr() {
        return getAll().toArray(Item[]::new);
    }

    public List<Item> getAll() {
        return all.values().stream().map(Supplier::get).toList();
    }

    public Item get(Tier variant) {
        return this.all.get(variant).get();
    }

    @FunctionalInterface
    public interface Factory {
        Item get(Tier variant, Item.Properties properties);
    }
}
