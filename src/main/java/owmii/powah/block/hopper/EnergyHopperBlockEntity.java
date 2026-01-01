package owmii.powah.block.hopper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import owmii.powah.block.Tiles;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.PowahBaseEnergyStorageBlockEntity;
import owmii.powah.util.ChargeUtil;

public class EnergyHopperBlockEntity extends PowahBaseEnergyStorageBlockEntity<EnergyHopperBlock> implements IInventoryHolder {
    public EnergyHopperBlockEntity(BlockPos pos, BlockState state) {
        super(Tiles.ENERGY_HOPPER.get(), pos, state);
    }

    @Override
    protected int postTick(Level world) {
        long extracted = 0;
        if (!isRemote() && checkRedstone()) {
            Direction side = getBlockState().getValue(BlockStateProperties.FACING);
            BlockEntity tile = world.getBlockEntity(this.worldPosition.relative(side));
            if (tile instanceof Container container) {
                try (var tx = Transaction.openRoot()) {
                    extracted = ChargeUtil.chargeItemsInContainer(container, getBlock().getChargingRate(), getEnergy().getStored(), tx);
                    getEnergy().extractEnergy(extracted, tx);
                    tx.commit();
                }
            }
        }
        return extracted > 0 ? 5 : super.postTick(world);
    }

    @Override
    public boolean keepEnergy() {
        return true;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack) {
        return false;
    }
}
