package owmii.powah.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.magmator.MagmatorBlockEntity;
import owmii.powah.lib.logistics.inventory.BaseEnergyMenu;
import owmii.powah.lib.logistics.inventory.slot.SlotBase;

public class MagmatorMenu extends BaseEnergyMenu<MagmatorBlockEntity> {
    public MagmatorMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.MAGMATOR.get(), id, inventory, buffer);
    }

    public MagmatorMenu(int id, Inventory inventory, MagmatorBlockEntity te) {
        super(Containers.MAGMATOR.get(), id, inventory, te);
    }

    public static MagmatorMenu create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new MagmatorMenu(id, inventory, buffer);
    }

    @Override
    protected void init(Inventory inventory, MagmatorBlockEntity te) {
        super.init(inventory, te);
        addSlot(new SlotBase(te.getInventory(), 0, 4, 54));
        addPlayerInventory(inventory, 8, 84, 4);
    }
}
