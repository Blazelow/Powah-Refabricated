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
import org.jetbrains.annotations.Nullable;
import owmii.powah.lib.block.AbstractTileEntity;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.ITankHolder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import owmii.powah.network.Network;
import owmii.powah.network.packet.InteractWithTankPacket;

public abstract class AbstractTileContainer<T extends AbstractTileEntity<?, ?> & IInventoryHolder> extends AbstractContainer {
    public final T te;

    public AbstractTileContainer(@Nullable MenuType<?> containerType, int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(containerType, id, inventory, getInventory(inventory.player, buffer.readBlockPos()));
    }

    public AbstractTileContainer(@Nullable MenuType<?> type, int id, Inventory inventory, T te) {
        super(type, id, inventory);
        this.te = te;
        init(inventory, te);
        this.te.setContainerOpen(true);
    }

    @Override
    protected final void init(Inventory inventory) {
        super.init(inventory);
    }

    protected void init(Inventory inventory, T te) {

    }

    @SuppressWarnings("unchecked")
    protected static <T extends AbstractTileEntity<?, ?>> T getInventory(Player player, BlockPos pos) {
        BlockEntity tile = player.level().getBlockEntity(pos);
        if (tile instanceof AbstractTileEntity<?, ?>)
            return (T) tile;
        return (T) new AbstractTileEntity<>(BlockEntityType.SIGN, pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.te.setContainerOpen(false);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack1 = slot.getItem();
            stack = stack1.copy();
            int size = this.te.getInventory().getSlots();
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
        if (player.level().isClientSide()) {
            Network.toServer(new InteractWithTankPacket(containerId, drain));
            return;
        }

        var carried = getCarried();
        if (carried.isEmpty()) {
            return;
        }

        if (!(te instanceof ITankHolder tankHolder)) {
            return;
        }

        var tank = tankHolder.getTank();
        if (tank.getCapacity() == 0) {
            return;
        }

        // FAPI fluid container interaction - replaces NeoForge FluidUtil.tryFillContainerAndStow/tryEmptyContainer
        try (var transaction = net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()) {
            var ctx = net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.ofPlayerCursor(player, this);
            var itemFluid = net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.ITEM.find(carried, ctx);
            if (itemFluid != null) {
                var storage = tank.asFabricStorage();
                long transferred;
                if (drain) {
                    transferred = net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.move(
                            storage, itemFluid, f -> true, Long.MAX_VALUE, transaction);
                } else {
                    transferred = net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.move(
                            itemFluid, storage, f -> true, Long.MAX_VALUE, transaction);
                }
                if (transferred > 0) {
                    transaction.commit();
                    broadcastChanges();
                }
            }
        }
    }
}
