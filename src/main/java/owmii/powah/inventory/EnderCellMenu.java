package owmii.powah.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.ender.PowahBaseEnderBlockEntity;
import owmii.powah.lib.logistics.inventory.BaseEnergyMenu;
import owmii.powah.lib.logistics.inventory.slot.SlotBase;

public class EnderCellMenu extends BaseEnergyMenu<PowahBaseEnderBlockEntity<?>> {
    public EnderCellMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.ENDER_CELL.get(), id, inventory, buffer);
    }

    public EnderCellMenu(int id, Inventory inventory, PowahBaseEnderBlockEntity te) {
        super(Containers.ENDER_CELL.get(), id, inventory, te);
    }

    public static EnderCellMenu create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new EnderCellMenu(id, inventory, buffer);
    }

    @Override
    protected void init(Inventory inventory, PowahBaseEnderBlockEntity te) {
        super.init(inventory);
        addSlot(new SlotBase(te.getInventory(), 0, 0, 1000));
        addSlot(new SlotBase(te.getInventory(), 1, 4, 4));
        addSlot(new SlotBase(te.getInventory(), 2, 4, 29));
        addPlayerInventory(inventory, 8, 82, 4);
    }
}
