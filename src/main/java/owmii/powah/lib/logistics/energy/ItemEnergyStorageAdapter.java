package owmii.powah.lib.logistics.energy;

import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

final class ItemEnergyStorageAdapter implements EnergyHandler {
    private final Energy.Item energyItem;

    public ItemEnergyStorageAdapter(Energy.Item energyItem) {
        this.energyItem = energyItem;
    }

    @Override
    public long getAmountAsLong() {
        return energyItem.getEnergyStored();
    }

    @Override
    public long getCapacityAsLong() {
        return energyItem.getMaxEnergyStored();
    }

    @Override
    public int insert(int amount, TransactionContext tx) {
        if (energyItem.getCapacity() == 0 || !energyItem.canReceive()) {
            return 0;
        }
        return 0; // TODO 26.1 return Ints.saturatedCast(energyItem.receiveEnergy(amount, bl));
    }

    @Override
    public int extract(int amount, TransactionContext tx) {
        if (!energyItem.canExtract()) {
            return 0;
        }
        return 0; // TODO 26.1 return Ints.saturatedCast(energyItem.extractEnergy(i, bl));
    }
}
