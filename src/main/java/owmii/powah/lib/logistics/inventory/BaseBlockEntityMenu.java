package owmii.powah.lib.logistics.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.PowahBaseBlockEntity;
import owmii.powah.lib.logistics.fluid.FluidUtil;
import owmii.powah.network.Network;
import owmii.powah.network.packet.InteractWithTankPacket;

public abstract class BaseBlockEntityMenu<T extends PowahBaseBlockEntity<?> & IInventoryHolder> extends BaseMenu {
    public final T blockEntity;

    public BaseBlockEntityMenu(@Nullable MenuType<?> containerType, int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerType, id, inventory, getInventory(inventory.player, buffer.readBlockPos()));
    }

    public BaseBlockEntityMenu(@Nullable MenuType<?> type, int id, Inventory inventory, T blockEntity) {
        super(type, id, inventory);
        this.blockEntity = blockEntity;
        init(inventory, blockEntity);
        this.blockEntity.setContainerOpen(true);
    }

    @Override
    protected final void init(Inventory inventory) {
        super.init(inventory);
    }

    protected void init(Inventory inventory, T te) {

    }

    @SuppressWarnings("unchecked")
    protected static <T extends PowahBaseBlockEntity<?>> T getInventory(Player player, BlockPos pos) {
        BlockEntity tile = player.level().getBlockEntity(pos);
        if (tile instanceof PowahBaseBlockEntity<?>)
            return (T) tile;
        // What the hell is this?
        return (T) new PowahBaseBlockEntity<>(BlockEntityType.SIGN, pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.blockEntity.setContainerOpen(false);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            int size = this.blockEntity.getInventory().size();
            if (index < size) {
                if (!moveItemStackTo(stack1, size, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(stack1, 0, size, false)) {
                return ItemStack.EMPTY;
            }
            if (stack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
                slot.onTake(this.player, stack);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    public void interactWithTank(boolean drain) {
        var level = player.level();
        if (level.isClientSide()) {
            Network.toServer(new InteractWithTankPacket(containerId, drain));
            return;
        }

        var tank = blockEntity.getTank();
        if (tank.getCapacity() == 0) {
            return;
        }

        if (FluidUtil.interactWithCarried(player, this, tank, drain)) {
            this.blockEntity.sync();
        }
    }
}
