package owmii.powah.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.furnator.FurnatorBlockEntity;
import owmii.powah.lib.logistics.inventory.BaseEnergyMenu;
import owmii.powah.lib.logistics.inventory.slot.SlotBase;

public class FurnatorMenu extends BaseEnergyMenu<FurnatorBlockEntity> {
    public FurnatorMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.FURNATOR.get(), id, inventory, buffer);
    }

    public FurnatorMenu(int id, Inventory inventory, FurnatorBlockEntity te) {
        super(Containers.FURNATOR.get(), id, inventory, te);
    }

    public static FurnatorMenu create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new FurnatorMenu(id, inventory, buffer);
    }

    @Override
    protected void init(Inventory inventory, FurnatorBlockEntity te) {
        super.init(inventory, te);
        addSlot(new SlotBase(te.getInventory(), 0, 4, 54));
        addSlot(new SlotBase(te.getInventory(), 1, 87, 18));
        addPlayerInventory(inventory, 8, 84, 4);
    }
}
