package owmii.powah.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.hopper.EnergyHopperBlockEntity;
import owmii.powah.lib.logistics.inventory.BaseEnergyMenu;

public class EnergyHopperMenu extends BaseEnergyMenu<EnergyHopperBlockEntity> {
    public EnergyHopperMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.ENERGY_HOPPER.get(), id, inventory, buffer);
    }

    public EnergyHopperMenu(int id, Inventory inventory, EnergyHopperBlockEntity te) {
        super(Containers.ENERGY_HOPPER.get(), id, inventory, te);
    }

    public static EnergyHopperMenu create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new EnergyHopperMenu(id, inventory, buffer);
    }

    @Override
    protected void init(Inventory inventory, EnergyHopperBlockEntity te) {
        super.init(inventory);
        addPlayerInventory(inventory, 8, 59, 4);
    }
}
