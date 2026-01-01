package owmii.powah.block.energycell;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.Blcks;
import owmii.powah.block.Tier;
import owmii.powah.block.Tiles;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.PowahBaseEnergyStorageBlockEntity;
import owmii.powah.lib.logistics.Transfer;
import owmii.powah.util.ChargeUtil;

public class EnergyCellBlockEntity extends PowahBaseEnergyStorageBlockEntity<EnergyCellBlock> implements IInventoryHolder {
    public EnergyCellBlockEntity(BlockPos pos, BlockState state) {
        super(Tiles.ENERGY_CELL.get(), pos, state);
    }

    @Override
    protected int getInternalInventorySize() {
        return 2;
    }

    @Override
    protected void onFirstTick(Level world) {
        super.onFirstTick(world);
        if (isCreative()) {
            getEnergy().setStored(getEnergyCapacity());
        }
    }

    @Override
    protected int postTick(Level world) {
        return chargeItems(2) + extractFromSides(world) > 0 ? 10 : -1;
    }

    @Override
    public long extractEnergy(long maxExtract, TransactionContext tx, @Nullable Direction side) {
        if (isCreative()) {
            return maxExtract;
        }
        return super.extractEnergy(maxExtract, tx, side);
    }

    @Override
    public boolean canExtractEnergy(@Nullable Direction side) {
        return checkRedstone() && super.canExtractEnergy(side);
    }

    @Override
    public boolean canReceiveEnergy(@Nullable Direction side) {
        return checkRedstone() && super.canReceiveEnergy(side);
    }

    @Override
    public boolean keepEnergy() {
        return !isCreative();
    }

    @Override
    public Transfer getTransferType() {
        return isCreative() ? Transfer.EXTRACT : super.getTransferType();
    }

    public boolean isCreative() {
        return getBlockState().is(Blcks.ENERGY_CELL.get(Tier.CREATIVE));
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack) {
        return ChargeUtil.isChargeableItem(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack) {
        return true;
    }
}
