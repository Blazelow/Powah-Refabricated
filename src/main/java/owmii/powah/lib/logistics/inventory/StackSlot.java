package owmii.powah.lib.logistics.inventory;

import net.fabricmc.fabric.api.transfer.v1.item.base.SingleStackStorage;
import net.minecraft.world.item.ItemStack;

public class StackSlot extends SingleStackStorage {
    private final ItemStack stack;

    public StackSlot(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    protected ItemStack getStack() {
        return stack;
    }

    @Override
    protected void setStack(ItemStack newStack) {
        if (newStack.isEmpty()) {
            stack.setCount(0);
            return;
        }
        stack.setCount(newStack.getCount());
        stack.applyComponents(newStack.getComponentsPatch());
    }
}
