package owmii.powah.lib.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.lib.block.PowahBaseEnergyBlock;
import owmii.powah.lib.logistics.Transfer;

public class EnergyBlockItem<B extends PowahBaseEnergyBlock<B>> extends PowahBlockItem<B>
        implements IEnergyItemProvider, IEnergyContainingItem {
    public EnergyBlockItem(B block, Properties builder, @Nullable ResourceKey<CreativeModeTab> group) {
        super(block, builder, group);
    }

    @Override
    public Info getEnergyInfo() {
        var transfer = getBlock().getEnergyTransfer();
        return new Info(getBlock().getEnergyCapacity(),
                getTransferType().canReceive ? transfer : 0,
                getTransferType().canExtract ? transfer : 0);
    }

    public Transfer getTransferType() {
        return getBlock().getTransferType();
    }

    public Tier getTier() {
        return getBlock().getTier();
    }

    @Override
    public boolean isChargeable(ItemStack stack) {
        return getBlock().isChargeable(stack);
    }
}
