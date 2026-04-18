package owmii.powah.lib.logistics.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import owmii.powah.lib.block.AbstractTileEntity;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.item.Stacks;
import owmii.powah.util.Util;

public class Inventory extends ItemStackHandler {
    @Nullable
    private IInventoryHolder tile;

    public Inventory(int size) {
        this(size, null);
    }

    public Inventory(Stacks stacks) {
        this(stacks, null);
    }

    public Inventory(Stacks stacks, @Nullable IInventoryHolder tile) {
        super(stacks);
        this.tile = tile;
    }

    Inventory(int size, @Nullable IInventoryHolder tile) {
        super(size);
        this.tile = tile;
    }

    public static <I extends AbstractTileEntity & owmii.powah.lib.block.IInventoryHolder> Inventory create(int size, @Nullable I tile) {
        return new Inventory(size, tile);
    }

    public static <I extends AbstractTileEntity & owmii.powah.lib.block.IInventoryHolder> Inventory createBlank(@Nullable I tile) {
        return new Inventory(0, tile);
    }

    public static Inventory create(int size) {
        return new Inventory(size, null);
    }

    public static Inventory createBlank() {
        return new Inventory(0, null);
    }

    public void setTile(@Nullable IInventoryHolder tile) {
        this.tile = tile;
    }

    public void deserializeNBT(CompoundTag nbt, HolderLookup.Provider registries) {
        if (isBlank())
            return;
        nbt.putInt("Size", getSlots());
        super.deserializeNBT(nbt, registries);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        return isBlank() ? new CompoundTag() : super.serializeNBT(registries);
    }

    public Inventory set(int size) {
        this.stacks = Stacks.withSize(size, ItemStack.EMPTY);
        onContentsChanged(0);
        return this;
    }

    public Inventory add(int size) {
        this.stacks = Stacks.withSize(size + this.stacks.size(), ItemStack.EMPTY);
        return this;
    }

    public Stacks canPut(Stacks outputs, int... slots) {
        return canPut(outputs, fromSlotArray(slots));
    }

    public Stacks canPut(Stacks outputs, Stacks slots) {
        Inventory inv = new Inventory(Stacks.from(slots).copy());
        for (ItemStack stack : outputs) {
            if (!ItemStackHandlerHelper.insertItem(inv, stack.copy(), false).isEmpty()) {
                return Stacks.create();
            }
        }
        return inv.getStacks();
    }

    public Stacks fromSlotArray(int... slots) {
        Stacks stacks = Stacks.create();
        for (int i : slots) {
            stacks.add(getStackInSlot(i));
        }
        return stacks;
    }

    @Override
    public int getSlotLimit(int slot) {
        if (this.tile != null) {
            return this.tile.getSlotLimit(slot);
        }
        return super.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (this.tile != null) {
            return this.tile.canInsert(slot, stack);
        }
        return super.isItemValid(slot, stack);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return canExtract(slot, getStackInSlot(slot)) ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
    }

    public ItemStack extractItemFromSlot(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }

    public boolean canExtract(int slot, ItemStack stack) {
        if (this.tile != null) {
            return this.tile.canExtract(slot, stack);
        }
        return true;
    }

    private boolean sendUpdates = true;

    public void setSendUpdates(boolean sendUpdates) {
        this.sendUpdates = sendUpdates;
    }

    @Override
    public void onContentsChanged(int slot) {
        if (this.tile != null && sendUpdates) {
            this.tile.onSlotChanged(slot);
        }
    }

    public ItemStack getFirst() {
        return getStackInSlot(0);
    }

    public ItemStack getLast() {
        return getStackInSlot(getLastSlot());
    }

    public int getLastSlot() {
        return getSlots() - 1;
    }

    public Stacks getLast(int count) {
        Stacks stacks = Stacks.create();
        int size = this.stacks.size();
        for (int i = size - count; i < count; i++) {
            stacks.add(this.stacks.get(i));
        }
        return stacks;
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {
        for (ItemStack stack : this.stacks) {
            if (stack.getCount() < stack.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }

    public boolean hasEmptySlot() {
        for (ItemStack stack : this.stacks) {
            if (stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSlotEmpty(int slot) {
        return this.stacks.get(slot).isEmpty();
    }

    public ItemStack setSlotEmpty(int slot) {
        ItemStack stack = this.stacks.set(slot, ItemStack.EMPTY);
        onContentsChanged(slot);
        return stack;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        ItemStack stack1 = this.stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    public void clear() {
        set(getSlots());
    }

    public boolean isBlank() {
        return this.stacks.isEmpty();
    }

    public Stacks getStacks() {
        return Stacks.from(this.stacks);
    }

    public List<ItemStack> getNonEmptyStacks() {
        List<ItemStack> stacks = new ArrayList<>(this.stacks);
        stacks.removeIf(ItemStack::isEmpty);
        return stacks;
    }

    public ItemStack addNext(ItemStack stack) {
        for (int i = 0; i < getSlots(); ++i) {
            if (isItemValid(i, stack)) {
                insertItem(i, stack.copy(), false);
                return stack.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack removeNext() {
        for (int i = getSlots() - 1; i >= 0; --i) {
            ItemStack stack = setSlotEmpty(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack insertItem(ItemStack stack, boolean simulate, int... ex) {
        if (stack.isEmpty())
            return stack;
        for (int i = 0; i < getSlots(); i++) {
            if (Util.anyMatch(ex, i))
                continue;
            stack = insertItem(i, stack, simulate);
            if (stack.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    public void drop(Level world, BlockPos pos) {
        this.stacks.forEach(stack -> {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
        });
        clear();
    }

    public void drop(int index, Level world, BlockPos pos) {
        ItemStack stack = getStackInSlot(index);
        if (!stack.isEmpty()) {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
            setStackInSlot(index, ItemStack.EMPTY);
        }
    }

    public static int calcRedstone(ItemStackHandler inv) {
        int i = 0;
        float f = 0.0F;
        for (int j = 0; j < inv.getSlots(); ++j) {
            ItemStack itemstack = inv.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                f += (float) itemstack.getCount() / (float) Math.min(inv.getSlotLimit(j), itemstack.getMaxStackSize());
                ++i;
            }
        }
        f = f / (float) inv.getSlots();
        return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
    }

    /**
     * Returns a FAPI Storage<ItemVariant> wrapping this inventory.
     * Used by ItemStorage.SIDED capability registration in Powah.java.
     * Returns null if the inventory is blank (empty slot count).
     */
    @SuppressWarnings("UnstableApiUsage")
    public Storage<ItemVariant> asFabricStorage() {
        if (isBlank()) return null;
        return new FabricInventoryStorage();
    }

    @SuppressWarnings("UnstableApiUsage")
    private final class FabricInventoryStorage implements Storage<ItemVariant> {
        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            int remain = (int) Math.min(maxAmount, Integer.MAX_VALUE);
            ItemStack toInsert = resource.toStack(remain);
            ItemStack leftover = insertItem(toInsert.copy(), true);
            int inserted = remain - leftover.getCount();
            if (inserted > 0) {
                transaction.addCloseCallback((t, result) -> {
                    if (result.wasCommitted()) {
                        insertItem(resource.toStack(inserted), false);
                    }
                });
            }
            return inserted;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            int total = 0;
            int limit = (int) Math.min(maxAmount, Integer.MAX_VALUE);
            // simulate first pass
            for (int i = 0; i < getSlots() && total < limit; i++) {
                ItemStack stack = getStackInSlot(i);
                if (!stack.isEmpty() && resource.matches(stack)) {
                    int can = Math.min(stack.getCount(), limit - total);
                    total += can;
                }
            }
            if (total > 0) {
                int finalTotal = total;
                transaction.addCloseCallback((t, result) -> {
                    if (result.wasCommitted()) {
                        int remaining = finalTotal;
                        for (int i = 0; i < getSlots() && remaining > 0; i++) {
                            ItemStack stack = getStackInSlot(i);
                            if (!stack.isEmpty() && resource.matches(stack)) {
                                int take = Math.min(stack.getCount(), remaining);
                                extractItem(i, take, false);
                                remaining -= take;
                            }
                        }
                    }
                });
            }
            return total;
        }

        @Override
        public java.util.Iterator<net.fabricmc.fabric.api.transfer.v1.storage.StorageView<ItemVariant>> iterator() {
            return new java.util.Iterator<net.fabricmc.fabric.api.transfer.v1.storage.StorageView<ItemVariant>>() {
                int i = 0;
                @Override public boolean hasNext() { return i < getSlots(); }
                @Override public SingleSlotStorage<ItemVariant> next() {
                    final int slot = i++;
                    return new SingleSlotStorage<>() {
                        @Override public long insert(ItemVariant r, long max, TransactionContext tx) {
                            return FabricInventoryStorage.this.insert(r, max, tx);
                        }
                        @Override public long extract(ItemVariant r, long max, TransactionContext tx) {
                            return FabricInventoryStorage.this.extract(r, max, tx);
                        }
                        @Override public boolean isResourceBlank() { return getStackInSlot(slot).isEmpty(); }
                        @Override public ItemVariant getResource() {
                            return ItemVariant.of(getStackInSlot(slot));
                        }
                        @Override public long getAmount() { return getStackInSlot(slot).getCount(); }
                        @Override public long getCapacity() { return getSlotLimit(slot); }
                    };
                }
            };
        }

        @Override
        public boolean supportsInsertion() { return true; }
        @Override public boolean supportsExtraction() { return true; }
    }

}