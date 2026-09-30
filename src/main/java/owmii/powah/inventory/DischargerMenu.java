package owmii.powah.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.discharger.EnergyDischargerBlockEntity;
import owmii.powah.lib.logistics.inventory.BaseEnergyMenu;
import owmii.powah.lib.logistics.inventory.slot.SlotBase;

public class DischargerMenu extends BaseEnergyMenu<EnergyDischargerBlockEntity> {
    public DischargerMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.DISCHARGER.get(), id, inventory, buffer);
    }

    public DischargerMenu(int id, Inventory inventory, EnergyDischargerBlockEntity te) {
        super(Containers.DISCHARGER.get(), id, inventory, te);
    }

    public static DischargerMenu create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new DischargerMenu(id, inventory, buffer);
    }

    @Override
    protected void init(Inventory inventory, EnergyDischargerBlockEntity te) {
        super.init(inventory);
        for (int i = 0; i < 7; i++) {
            addSlot(new SlotBase(te.getInventory(), i, 5 + (i * 25), 54));
        }
        addPlayerInventory(inventory, 8, 84, 4);
    }
}
