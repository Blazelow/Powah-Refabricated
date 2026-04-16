package owmii.powah.lib.logistics.inventory;

import net.minecraft.world.item.ItemStack;

/**
 * Local replacement for NeoForge's net.neoforged.neoforge.items.IItemHandler.
 * Defines the same slot-based item handler contract used internally by Powah.
 */
public interface IItemHandler {
    int getSlots();

    ItemStack getStackInSlot(int slot);

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    int getSlotLimit(int slot);

    boolean isItemValid(int slot, ItemStack stack);
}
