package owmii.powah.lib.logistics.energy;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import owmii.powah.components.PowahComponents;
import owmii.powah.lib.item.IEnergyContainingItem;
import team.reborn.energy.api.EnergyStorage;

public final class ItemEnergyStorage implements EnergyStorage {
    private final ContainerItemContext context;
    private final Item validItem;
    private final IEnergyContainingItem.Info info;

    public ItemEnergyStorage(ContainerItemContext context, IEnergyContainingItem.Info info) {
        this.context = context;
        this.validItem = context.getItemVariant().getItem();
        this.info = info;
    }

    private boolean usable() {
        return context.getAmount() == 1 && info.capacity() > 0 && context.getItemVariant().getItem() == validItem;
    }

    @Override
    public long getAmount() {
        var variant = context.getItemVariant();
        if (variant.getItem() != validItem) {
            return 0;
        }
        Long stored = variant.toStack().get(PowahComponents.ENERGY_STORED);
        return stored == null ? 0 : Math.max(0, Math.min(stored, info.capacity()));
    }

    @Override
    public long getCapacity() {
        return context.getItemVariant().getItem() == validItem ? info.capacity() : 0;
    }

    @Override
    public boolean supportsInsertion() {
        return info.maxInsert() > 0;
    }

    @Override
    public boolean supportsExtraction() {
        return info.maxExtract() > 0;
    }

    @Override
    public long insert(long maxAmount, TransactionContext transaction) {
        if (maxAmount <= 0 || !usable()) {
            return 0;
        }
        long amount = Math.min(info.capacity() - getAmount(), Math.min(info.maxInsert(), maxAmount));
        return amount > 0 && change(getAmount() + amount, transaction) ? amount : 0;
    }

    @Override
    public long extract(long maxAmount, TransactionContext transaction) {
        if (maxAmount <= 0 || !usable()) {
            return 0;
        }
        long amount = Math.min(getAmount(), Math.min(info.maxExtract(), maxAmount));
        return amount > 0 && change(getAmount() - amount, transaction) ? amount : 0;
    }

    private boolean change(long newAmount, TransactionContext transaction) {
        ItemStack stack = context.getItemVariant().toStack();
        stack.set(PowahComponents.ENERGY_STORED, newAmount);
        return context.exchange(ItemVariant.of(stack), 1, transaction) == 1;
    }
}
