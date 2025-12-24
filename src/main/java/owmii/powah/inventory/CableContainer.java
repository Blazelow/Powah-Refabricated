package owmii.powah.inventory;

import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import owmii.powah.block.cable.CableBlockEntity;
import owmii.powah.lib.logistics.inventory.AbstractEnergyContainer;

public class CableContainer extends AbstractEnergyContainer<CableBlockEntity> {
    private Direction side = Direction.NORTH;

    public CableContainer(int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(Containers.CABLE.get(), id, inventory, buffer);
        this.side = Direction.from3DDataValue(buffer.readInt());
    }

    public CableContainer(int id, Inventory inventory, CableBlockEntity te) {
        super(Containers.CABLE.get(), id, inventory, te);
    }

    public static CableContainer create(int id, Inventory inventory, FriendlyByteBuf buffer) {
        return new CableContainer(id, inventory, buffer);
    }

    public Direction getSide() {
        return this.side;
    }
}
