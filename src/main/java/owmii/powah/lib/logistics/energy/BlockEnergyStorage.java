package owmii.powah.lib.logistics.energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;
import owmii.powah.lib.block.AbstractEnergyStorage;
import team.reborn.energy.api.EnergyStorage;

public final class BlockEnergyStorage implements EnergyStorage {
    private final AbstractEnergyStorage<?, ?> tile;
    private final @Nullable Direction side;

    public BlockEnergyStorage(AbstractEnergyStorage<?, ?> tile, @Nullable Direction side) {
        this.tile = tile;
        this.side = side;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        if (maxAmount <= 0) {
            return 0;
        }
        long received = tile.receiveEnergy(maxAmount, true, side);
        if (received > 0) {
            transaction.addCloseCallback((context, result) -> {
                if (result.wasCommitted()) {
                    tile.receiveEnergy(received, false, side);
                }
            });
        }
        return received;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        if (maxAmount <= 0) {
            return 0;
        }
        long extracted = tile.extractEnergy(maxAmount, true, side);
        if (extracted > 0) {
            transaction.addCloseCallback((context, result) -> {
                if (result.wasCommitted()) {
                    tile.extractEnergy(extracted, false, side);
                }
            });
        }
        return extracted;
    }

    public long insertNative(long maxAmount, boolean simulate) {
        return maxAmount <= 0 ? 0 : tile.receiveEnergy(maxAmount, simulate, side);
    }

    @Override
    public long getAmount() {
        return tile.getEnergy().getStored();
    }

    @Override
    public long getCapacity() {
        return tile.getEnergy().getMaxEnergyStored();
    }

    @Override
    public boolean supportsInsertion() {
        return tile.canReceiveEnergy(side);
    }

    @Override
    public boolean supportsExtraction() {
        return tile.canExtractEnergy(side);
    }
}
