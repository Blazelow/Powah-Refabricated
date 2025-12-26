package owmii.powah.lib.logistics.inventory.slot;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import owmii.powah.lib.logistics.inventory.ItemStackHandler;

public class SlotBase extends ResourceHandlerSlot {
    private final ItemStackHandler handler;

    public SlotBase(ItemStackHandler handler, int id, int x, int y) {
        super(handler, handler::set, id, x, y);
        this.handler = handler;
    }

    public void initialize(ItemStack stack) {
        handler.setStackInSlot(getContainerSlot(), stack);
    }
}
