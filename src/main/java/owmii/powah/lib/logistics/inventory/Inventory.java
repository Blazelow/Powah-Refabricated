package owmii.powah.lib.logistics.inventory;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.PowahBaseBlockEntity;

public class Inventory extends ItemStackHandler {
    @Nullable
    private IInventoryHolder tile;

    public Inventory(int size) {
        this(size, null);
    }

    Inventory(int size, @Nullable IInventoryHolder tile) {
        super(size);
        this.tile = tile;
    }

    public static <I extends PowahBaseBlockEntity & owmii.powah.lib.block.IInventoryHolder> Inventory create(int size, @Nullable I tile) {
        return new Inventory(size, tile);
    }

    public static <I extends PowahBaseBlockEntity & owmii.powah.lib.block.IInventoryHolder> Inventory createBlank(@Nullable I tile) {
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

    public void deserialize(ValueInput input) {
        super.deserialize(input);
        if (stacks.size() != size()) {
            throw new IllegalStateException("Inventory size has changed!");
        }
    }

    @Override
    public long getCapacityAsLong(int index, ItemResource resource) {
        return super.getCapacityAsLong(index, resource);
    }

    @Override
    protected int getSlotLimit(int slot) {
        if (this.tile != null) {
            return this.tile.getSlotLimit(slot);
        }
        return super.getSlotLimit(slot);
    }

    public final boolean isItemValid(int slot, ItemStack stack) {
        return isValid(slot, ItemResource.of(stack));
    }

    @Override
    public boolean isValid(int slot, ItemResource resource) {
        if (this.tile != null) {
            return this.tile.canInsert(slot, resource.toStack());
        }
        return super.isValid(slot, resource);
    }

    @Override
    public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (!canExtract(index, resource.toStack())) {
            return 0;
        }
        return super.extract(index, resource, amount, transaction);
    }

    public ItemStack extractItemFromSlot(int slot, int amount, boolean simulate) {
        try (var tx = Transaction.openRoot()) {
            var what = getResource(slot);
            var extracted = extract(slot, what, amount, tx);
            if (!simulate) {
                tx.commit();
            }
            return what.toStack(extracted);
        }
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
    public void onContentsChanged(int index, ItemStack previousContents) {
        if (this.tile != null && sendUpdates) {
            this.tile.onSlotChanged(index);
        }
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
        onContentsChanged(slot, stack);
        return stack;
    }

    public void clear() {
        boolean changed = false;
        for (int i = 0; i < size(); i++) {
            changed |= !this.stacks.set(i, ItemStack.EMPTY).isEmpty();
        }
        if (changed) {
            onContentsChanged(0, ItemStack.EMPTY);
        }
    }

    public List<ItemStack> getNonEmptyStacks() {
        List<ItemStack> stacks = new ArrayList<>(this.stacks);
        stacks.removeIf(ItemStack::isEmpty);
        return stacks;
    }

    public ItemStack addNext(ItemStack stack) {
        try (var tx = Transaction.openRoot()) {
            var inserted = insert(ItemResource.of(stack), stack.getCount(), tx);
            var result = stack.copyWithCount(inserted);
            tx.commit();
            return result;
        }
    }

    public ItemStack removeNext() {
        for (int i = size() - 1; i >= 0; --i) {
            ItemStack stack = setSlotEmpty(i);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
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
}
