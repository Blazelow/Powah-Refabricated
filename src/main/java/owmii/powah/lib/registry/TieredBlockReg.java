package owmii.powah.lib.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;
import owmii.powah.block.Tier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class TieredBlockReg {
    private static final Map<String, List<String>> ALL_VARIANTS = new HashMap<>();

    public static List<String> getSiblingIds(String name) {
        return ALL_VARIANTS.getOrDefault(name, List.of(name));
    }

    private final LinkedHashMap<Tier, Supplier<Block>> all = new LinkedHashMap<>();

    public TieredBlockReg(DeferredRegister.Blocks dr, String name, Factory factory, Tier[] variants) {
        for (Tier variant : variants) {
            var entryName = name + "_" + variant.getName();
            ALL_VARIANTS.computeIfAbsent(name, s -> new ArrayList<>()).add(entryName);
            this.all.put(variant, dr.registerBlock(entryName, props -> factory.get(variant, props)));
        }
    }

    public Block[] getArr() {
        return getAll().toArray(Block[]::new);
    }

    public List<Block> getAll() {
        return all.values().stream().map(Supplier::get).toList();
    }

    public Block get(Tier variant) {
        return this.all.get(variant).get();
    }

    @FunctionalInterface
    public interface Factory {
        Block get(Tier variant, BlockBehaviour.Properties properties);
    }
}
