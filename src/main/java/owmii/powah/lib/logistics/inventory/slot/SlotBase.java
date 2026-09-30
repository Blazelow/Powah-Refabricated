package owmii.powah.lib.logistics.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import owmii.powah.lib.logistics.inventory.Inventory;

public class SlotBase extends Slot {
    private static final Container EMPTY_INVENTORY = new SimpleContainer(0);
    private final Inventory inventory;
    protected final int index;

    public SlotBase(Inventory inventory, int index, int xPosition, int yPosition) {
        super(EMPTY_INVENTORY, index, xPosition, yPosition);
        this.inventory = inventory;
        this.index = index;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        return inventory.isItemValid(index, stack);
    }

    @Override
    public ItemStack getItem() {
        return inventory.getStackInSlot(index);
    }

    @Override
    public void set(ItemStack stack) {
        inventory.setStackInSlot(index, stack);
        setChanged();
    }

    public void initialize(ItemStack stack) {
        inventory.setStackInSlot(index, stack);
    }

    @Override
    public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
    }

    @Override
    public int getMaxStackSize() {
        return inventory.getCapacityAsInt(this.index, ItemVariant.blank());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), inventory.getCapacityAsInt(this.index, ItemVariant.of(stack)));
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        var resource = inventory.getResource(index);
        if (resource.isBlank()) {
            return false;
        }
        try (var tx = Transaction.openOuter()) {
            return inventory.extract(index, resource, 1, tx) > 0;
        }
    }

    @Override
    public ItemStack remove(int amount) {
        var resource = inventory.getResource(index);
        if (resource.isBlank()) {
            return ItemStack.EMPTY;
        }
        try (var tx = Transaction.openOuter()) {
            var result = resource.toStack(inventory.extract(index, resource, amount, tx));
            tx.commit();
            return result;
        }
    }
}
