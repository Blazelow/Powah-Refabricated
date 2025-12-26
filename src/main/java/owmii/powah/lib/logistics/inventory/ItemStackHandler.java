/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package owmii.powah.lib.logistics.inventory;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class ItemStackHandler extends ItemStacksResourceHandler {
    public ItemStackHandler(int size) {
        super(size);
    }

    public ItemStack getStackInSlot(int slot) {
        return stacks.get(slot);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlotIndex(slot);
        var previous = this.stacks.set(slot, stack);
        onContentsChanged(slot, previous);
    }

    @Override
    protected int getCapacity(int index, ItemResource resource) {
        return Math.min(getSlotLimit(index), super.getCapacity(index, resource));
    }

    protected int getSlotLimit(int slot) {
        return 64;
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.size())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.size() + ")");
    }
}
