package owmii.powah.lib.logistics.energy;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import owmii.powah.components.PowahComponents;
import owmii.powah.lib.item.IEnergyContainingItem;

/**
 * Adapts {@link net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler} to use our long-based data component
 * and inherit the capacity from the item.
 */
public class EnergyItemHandler implements EnergyHandler {
    protected final ItemAccess itemAccess;
    protected final Item validItem;
    protected final IEnergyContainingItem.Info energyInfo;

    /**
     * Creates a new {@link net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler} instance.
     *
     * @param itemAccess item access backing this handler
     */
    public EnergyItemHandler(ItemAccess itemAccess, IEnergyContainingItem.Info energyInfo) {
        this.itemAccess = itemAccess;
        // Store the current item, such that if the item changes later we don't return any stored content from it.
        this.validItem = itemAccess.getResource().getItem();
        this.energyInfo = energyInfo;
    }

    private long getCapacity() {
        return energyInfo.capacity();
    }

    private long getMaxExtract() {
        return energyInfo.maxExtract();
    }

    private long getMaxInsert() {
        return energyInfo.maxInsert();
    }

    /**
     * Retrieves the amount stored in the {@linkplain ItemAccess#getResource() current contents} of the item access.
     */
    protected long getAmountFrom(ItemResource accessResource) {
        if (!accessResource.is(validItem)) {
            return 0;
        }
        return accessResource.getOrDefault(PowahComponents.ENERGY_STORED, 0L);
    }

    /**
     * Returns a resource with updated amount.
     *
     * @param accessResource current resource, before the update
     * @param newAmount      the new amount
     * @return {@code accessResource} updated with the new amount,
     *         or {@link ItemResource#EMPTY} if the new amount cannot be stored
     * @implNote This function <strong>should not</strong> mutate the {@linkplain #itemAccess item access},
     *           that will be done by the calling code based on the results of this function.
     */
    protected ItemResource update(ItemResource accessResource, long newAmount) {
        return accessResource.with(PowahComponents.ENERGY_STORED, newAmount);
    }

    @Override
    public long getAmountAsLong() {
        return itemAccess.getAmount() * getAmountFrom(itemAccess.getResource());
    }

    @Override
    public long getCapacityAsLong() {
        var accessResource = itemAccess.getResource();
        if (!accessResource.is(validItem)) {
            return 0;
        }

        return itemAccess.getAmount() * getCapacity();
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);

        int accessAmount = itemAccess.getAmount();
        if (accessAmount == 0) {
            return 0;
        }
        var amountPerItem = Math.min(getMaxInsert(), amount / accessAmount);
        if (amountPerItem == 0) {
            return 0;
        }

        ItemResource accessResource = itemAccess.getResource();
        if (!accessResource.is(validItem)) {
            return 0;
        }
        var currentAmountPerItem = getAmountFrom(accessResource);

        var insertedPerItem = Math.min(amountPerItem, getCapacity() - currentAmountPerItem);
        if (insertedPerItem > 0) {
            ItemResource filledResource = update(accessResource, currentAmountPerItem + insertedPerItem);

            if (!filledResource.isEmpty()) {
                return (int) (insertedPerItem * itemAccess.exchange(filledResource, accessAmount, transaction));
            }
        }

        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);

        var accessAmount = itemAccess.getAmount();
        if (accessAmount == 0) {
            return 0;
        }
        var amountPerItem = Math.min(getMaxExtract(), amount / accessAmount);
        if (amountPerItem == 0) {
            return 0;
        }

        ItemResource accessResource = itemAccess.getResource();
        // If the resource is not validItem this will return 0 and avoid extraction
        var currentAmountPerItem = getAmountFrom(accessResource);

        var extractedPerItem = Math.min(amountPerItem, currentAmountPerItem);
        if (extractedPerItem > 0) {
            ItemResource emptiedResource = update(accessResource, currentAmountPerItem - extractedPerItem);

            if (!emptiedResource.isEmpty()) {
                return (int) (extractedPerItem * itemAccess.exchange(emptiedResource, accessAmount, transaction));
            }
        }

        return 0;
    }
}
