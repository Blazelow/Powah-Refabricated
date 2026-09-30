package owmii.powah.lib.logistics.inventory;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedSlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ItemStackHandler {
    protected NonNullList<ItemStack> stacks;
    private final List<SlotStorage> slots = new ArrayList<>();
    private final Storage<ItemVariant> storage;

    public ItemStackHandler(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        for (int i = 0; i < size; i++) {
            slots.add(new SlotStorage(i));
        }
        this.storage = new CombinedSlottedStorage<>(slots);
    }

    public Storage<ItemVariant> getStorage() {
        return storage;
    }

    public SingleSlotStorage<ItemVariant> getSlot(int index) {
        return slots.get(index);
    }

    public int size() {
        return stacks.size();
    }

    public ItemStack getStackInSlot(int slot) {
        return stacks.get(slot);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        var previous = this.stacks.set(slot, stack);
        onContentsChanged(slot, previous);
    }

    public ItemVariant getResource(int slot) {
        return ItemVariant.of(stacks.get(slot));
    }

    public int getCapacityAsInt(int slot, ItemVariant resource) {
        int stackLimit = resource.isBlank() ? 99 : resource.toStack().getMaxStackSize();
        return Math.min(getSlotLimit(slot), stackLimit);
    }

    public int insert(ItemVariant resource, int amount, TransactionContext tx) {
        return (int) storage.insert(resource, amount, tx);
    }

    public int insert(int slot, ItemVariant resource, int amount, TransactionContext tx) {
        return (int) slots.get(slot).insert(resource, amount, tx);
    }

    public int extract(int slot, ItemVariant resource, int amount, TransactionContext tx) {
        return (int) slots.get(slot).extract(resource, amount, tx);
    }

    public boolean isValid(int slot, ItemVariant resource) {
        return true;
    }

    public boolean canExtract(int slot, ItemStack stack) {
        return true;
    }

    protected int getSlotLimit(int slot) {
        return 64;
    }

    protected void onContentsChanged(int index, ItemStack previousContents) {
    }

    public void serialize(ValueOutput output) {
        output.store("stacks", ItemStack.OPTIONAL_CODEC.listOf(), stacks);
    }

    public void deserialize(ValueInput input) {
        var loaded = input.read("stacks", ItemStack.OPTIONAL_CODEC.listOf()).orElse(List.of());
        for (int i = 0; i < stacks.size(); i++) {
            stacks.set(i, i < loaded.size() ? loaded.get(i) : ItemStack.EMPTY);
        }
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }

    private final class SlotStorage extends SingleStackStorage {
        private final int slot;

        private SlotStorage(int slot) {
            this.slot = slot;
        }

        @Override
        protected ItemStack getStack() {
            return stacks.get(slot);
        }

        @Override
        protected void setStack(ItemStack stack) {
            stacks.set(slot, stack);
        }

        @Override
        protected boolean canInsert(ItemVariant variant) {
            return isValid(slot, variant);
        }

        @Override
        protected boolean canExtract(ItemVariant variant) {
            return ItemStackHandler.this.canExtract(slot, variant.toStack());
        }

        @Override
        protected int getCapacity(ItemVariant variant) {
            return getCapacityAsInt(slot, variant);
        }

        @Override
        protected void onFinalCommit() {
            onContentsChanged(slot, ItemStack.EMPTY);
        }
    }
}
