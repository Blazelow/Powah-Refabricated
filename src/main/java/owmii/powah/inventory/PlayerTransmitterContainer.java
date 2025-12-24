package owmii.powah.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.transmitter.PlayerTransmitterBlockEntity;
import owmii.powah.lib.logistics.inventory.AbstractEnergyContainer;
import owmii.powah.lib.logistics.inventory.slot.SlotBase;

public class PlayerTransmitterContainer extends AbstractEnergyContainer<PlayerTransmitterBlockEntity> {
    public PlayerTransmitterContainer(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.PLAYER_TRANSMITTER.get(), id, inventory, buffer);
    }

    public PlayerTransmitterContainer(int id, Inventory inventory, PlayerTransmitterBlockEntity te) {
        super(Containers.PLAYER_TRANSMITTER.get(), id, inventory, te);
    }

    public static PlayerTransmitterContainer create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new PlayerTransmitterContainer(id, inventory, buffer);
    }

    @Override
    protected void init(Inventory inventory, PlayerTransmitterBlockEntity te) {
        super.init(inventory);
        addSlot(new SlotBase(te.getInventory(), 0, 4, 29));
        addPlayerInventory(inventory, 8, 59, 4);
    }
}
