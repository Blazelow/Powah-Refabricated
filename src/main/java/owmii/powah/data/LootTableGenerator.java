package owmii.powah.data;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import owmii.powah.block.Blcks;
import owmii.powah.item.Itms;

public class LootTableGenerator extends BlockLootSubProvider {
    public LootTableGenerator(HolderLookup.Provider providers) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), providers);
    }

    private Function<Block, LootTable.Builder> uraniniteOre(int dropCount) {
        return block -> {
            var fortune = registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
            return createSilkTouchDispatchTable(block,
                    applyExplosionDecay(block,
                            LootItem.lootTableItem(Itms.URANINITE_RAW)
                                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(dropCount)))
                                    .apply(ApplyBonusCount.addOreBonusCount(fortune))));
        };
    }

    @Override
    protected void generate() {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> biConsumer) {
        Map<Block, Function<Block, LootTable.Builder>> builders = new IdentityHashMap<>();

        builders.put(Blcks.DEEPSLATE_URANINITE_ORE_POOR, uraniniteOre(1));
        builders.put(Blcks.DEEPSLATE_URANINITE_ORE, uraniniteOre(2));
        builders.put(Blcks.DEEPSLATE_URANINITE_ORE_DENSE, uraniniteOre(4));
        builders.put(Blcks.URANINITE_ORE_POOR, uraniniteOre(1));
        builders.put(Blcks.URANINITE_ORE, uraniniteOre(2));
        builders.put(Blcks.URANINITE_ORE_DENSE, uraniniteOre(4));

        for (var entry : builders.entrySet()) {
            biConsumer.accept(entry.getKey().getLootTable(), entry.getValue().apply(entry.getKey()));
        }
    }
}
