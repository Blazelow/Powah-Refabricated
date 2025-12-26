package owmii.powah.block.discharger;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import owmii.powah.block.Tiles;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.PowahBaseEnergyStorageBlockEntity;
import owmii.powah.lib.logistics.Transfer;
import owmii.powah.util.ChargeUtil;

public class EnergyDischargerBlockEntity extends PowahBaseEnergyStorageBlockEntity<EnergyDischargerBlock> implements IInventoryHolder {
    public EnergyDischargerBlockEntity(BlockPos pos, BlockState state) {
        super(Tiles.ENERGY_DISCHARGER.get(), pos, state);
    }

    @Override
    protected int getInternalInventorySize() {
        return 7;
    }

    @Override
    protected int postTick(Level world) {
        long extracted = 0;
        if (!isRemote()) {
            if (checkRedstone()) {
                try (var tx = Transaction.openRoot()) {
                    extracted = ChargeUtil.dischargeItemsInInventory(this.inv, getEnergyTransfer(), getEnergyCapacity() - energy.getStored(), tx);
                    this.energy.insertEnergy(extracted, tx);
                    tx.commit();
                }
            }
            extracted += extractFromSides(world);
        }
        return extracted > 0 ? 5 : -1;
    }

    @Override
    public boolean keepEnergy() {
        return true;
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

    @Override
    public Transfer getTransferType() {
        return Transfer.EXTRACT;
    }
}
