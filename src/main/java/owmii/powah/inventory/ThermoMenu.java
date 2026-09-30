package owmii.powah.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.thermo.ThermoBlockEntity;
import owmii.powah.lib.logistics.inventory.BaseEnergyMenu;
import owmii.powah.lib.logistics.inventory.slot.SlotBase;

public class ThermoMenu extends BaseEnergyMenu<ThermoBlockEntity> {
    public ThermoMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.THERMO.get(), id, inventory, buffer);
    }

    public ThermoMenu(int id, Inventory inventory, ThermoBlockEntity te) {
        super(Containers.THERMO.get(), id, inventory, te);
    }

    public static ThermoMenu create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new ThermoMenu(id, inventory, buffer);
    }

    @Override
    protected void init(Inventory inventory, ThermoBlockEntity te) {
        super.init(inventory, te);
        addSlot(new SlotBase(te.getInventory(), 0, 4, 54));
        addPlayerInventory(inventory, 8, 84, 4);
    }
}
