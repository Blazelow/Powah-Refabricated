package owmii.powah.block.discharger;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.EnergyConfig;
import owmii.powah.inventory.DischargerContainer;
import owmii.powah.lib.block.PowahBaseEnergyBlock;
import owmii.powah.lib.block.PowahAbstractBlockEntity;
import owmii.powah.lib.item.EnergyBlockItem;
import owmii.powah.lib.logistics.Transfer;
import owmii.powah.lib.logistics.inventory.AbstractContainer;

public class EnergyDischargerBlock extends PowahBaseEnergyBlock<EnergyConfig, EnergyDischargerBlock> {
    public EnergyDischargerBlock(Properties properties, Tier variant) {
        super(properties, variant);
    }

    @Override
    public EnergyConfig getConfig() {
        return Powah.config().devices.dischargers;
    }

    @Override
    public EnergyBlockItem getBlockItem(Item.Properties properties, @Nullable ResourceKey<CreativeModeTab> group) {
        return super.getBlockItem(properties.stacksTo(1), group);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnergyDischargerBlockEntity(pos, state, this.variant);
    }

    @Nullable
    @Override
    public <T extends PowahAbstractBlockEntity> AbstractContainer getContainer(int id, Inventory inventory, PowahAbstractBlockEntity te, BlockHitResult result) {
        if (te instanceof EnergyDischargerBlockEntity) {
            return new DischargerContainer(id, inventory, (EnergyDischargerBlockEntity) te);
        }
        return null;
    }

    @Override
    public Transfer getTransferType() {
        return Transfer.EXTRACT;
    }

    @Override
    protected Facing getFacing() {
        return Facing.HORIZONTAL;
    }
}
