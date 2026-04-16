package owmii.powah.lib.logistics.energy;

import com.google.common.primitives.Ints;
import team.reborn.energy.api.EnergyStorage;

/**
 * Adapts Powah's internal Energy.Item to Team Reborn Energy's EnergyStorage API.
 * Replaces the old NeoForge IEnergyStorage adapter.
 */
public final class ItemEnergyStorageAdapter implements EnergyStorage {
    private final Energy.Item energyItem;

    public ItemEnergyStorageAdapter(Energy.Item energyItem) {
        this.energyItem = energyItem;
    }

    @Override
    public long insert(long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
        long simulated = energyItem.receiveEnergy(maxAmount, true);
        if (simulated > 0) {
            transaction.addCloseCallback((t, result) -> {
                if (result.wasCommitted()) {
                    energyItem.receiveEnergy(maxAmount, false);
                }
            });
        }
        return simulated;
    }

    @Override
    public long extract(long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
        long simulated = energyItem.extractEnergy(maxAmount, true);
        if (simulated > 0) {
            transaction.addCloseCallback((t, result) -> {
                if (result.wasCommitted()) {
                    energyItem.extractEnergy(maxAmount, false);
                }
            });
        }
        return simulated;
    }

    @Override
    public long getAmount() {
        return energyItem.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return energyItem.getMaxEnergyStored();
    }

    @Override
    public boolean supportsInsertion() {
        return energyItem.canReceive();
    }

    @Override
    public boolean supportsExtraction() {
        return energyItem.canExtract();
    }
}
