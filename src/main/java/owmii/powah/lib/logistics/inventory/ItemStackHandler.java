/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package owmii.powah.lib.logistics.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public class ItemStackHandler implements IItemHandler {
    protected NonNullList<ItemStack> stacks;

    public ItemStackHandler() {
        this(1);
    }

    public ItemStackHandler(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public ItemStackHandler(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public void setSize(int size) {
        stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty()) {
            if (!ItemStackHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate) {
            if (existing.isEmpty()) {
                // We must always copy the stack because onContentsChanged might change it, messing with our later use.
                this.stacks.set(slot, reachedLimit ? ItemStackHandlerHelper.copyStackWithSize(stack, limit) : stack.copy());
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemStackHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.stacks.set(slot, ItemStackHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemStackHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    public void save(ValueOutput output) {
        var list = output.childrenList("Items");
        for (int i = 0; i < stacks.size(); i++) {
            var stack = stacks.get(i);
            if (!stack.isEmpty()) {
                var entry = list.addChild();
                entry.store(ItemStack.MAP_CODEC, stack);
                entry.putInt("Slot", i);
            }
        }
        output.putInt("Size", stacks.size());
    }

    public void load(ValueInput input) {
        load(input, null);
    }

    public void load(ValueInput input, @Nullable Integer fixedSize) {
        if (fixedSize != null) {
            setSize(fixedSize);
        } else {
            setSize(input.getIntOr("Size", stacks.size()));
        }
        for (var entry : input.childrenListOrEmpty("Items")) {
            int slot = entry.getIntOr("Slot", 0);
            if (slot >= 0 && slot < stacks.size()) {
                stacks.set(slot, entry.read(ItemStack.MAP_CODEC).orElse(ItemStack.EMPTY));
            }
        }
        onLoad();
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }

    protected void onLoad() {

    }

    protected void onContentsChanged(int slot) {

    }
}
