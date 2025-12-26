package owmii.powah.lib.logistics.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import org.jspecify.annotations.Nullable;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.PowahBaseBlockEntity;

public class BaseEnergyMenu<T extends PowahBaseBlockEntity<?, ?> & IInventoryHolder> extends BaseBlockEntityMenu<T> {
    public BaseEnergyMenu(@Nullable MenuType<?> containerType, int id, Inventory inventory, FriendlyByteBuf buffer) {
        super(containerType, id, inventory, buffer);
    }

    public BaseEnergyMenu(@Nullable MenuType<?> type, int id, Inventory inventory, T te) {
        super(type, id, inventory, te);
    }
}
