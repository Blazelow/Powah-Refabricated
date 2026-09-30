package owmii.powah.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.solar.SolarBlockEntity;
import owmii.powah.lib.logistics.inventory.BaseEnergyMenu;

public class SolarMenu extends BaseEnergyMenu<SolarBlockEntity> {
    public SolarMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.SOLAR.get(), id, inventory, buffer);
    }

    public SolarMenu(int id, Inventory inventory, SolarBlockEntity te) {
        super(Containers.SOLAR.get(), id, inventory, te);
    }

    public static SolarMenu create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new SolarMenu(id, inventory, buffer);
    }

    @Override
    protected void init(Inventory inventory, SolarBlockEntity te) {
        super.init(inventory);
        addPlayerInventory(inventory, 8, 59, 4);
    }
}
