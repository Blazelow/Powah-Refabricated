package owmii.powah.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.energycell.EnergyCellBlockEntity;
import owmii.powah.lib.logistics.inventory.BaseEnergyMenu;
import owmii.powah.lib.logistics.inventory.slot.SlotBase;

public class EnergyCellMenu extends BaseEnergyMenu<EnergyCellBlockEntity> {
    public EnergyCellMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.ENERGY_CELL.get(), id, inventory, buffer);
    }

    public EnergyCellMenu(int id, Inventory inventory, EnergyCellBlockEntity te) {
        super(Containers.ENERGY_CELL.get(), id, inventory, te);
    }

    public static EnergyCellMenu create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new EnergyCellMenu(id, inventory, buffer);
    }

    @Override
    protected void init(Inventory inventory, EnergyCellBlockEntity te) {
        super.init(inventory);
        addSlot(new SlotBase(te.getInventory(), 0, 4, 4));
        addSlot(new SlotBase(te.getInventory(), 1, 4, 29));
        addPlayerInventory(inventory, 8, 59, 4);
    }
}
