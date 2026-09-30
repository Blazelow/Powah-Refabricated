package owmii.powah.lib.logistics.fluid;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public final class FluidUtil {
    private FluidUtil() {
    }

    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, FluidTank tank) {
        return FluidStorageUtil.interactWithFluidStorage(tank.getStorage(), player, hand);
    }

    public static boolean interactWithCarried(Player player, AbstractContainerMenu menu, FluidTank tank, boolean drain) {
        var context = ContainerItemContext.ofPlayerCursor(player, menu);
        Storage<FluidVariant> container = context.find(FluidStorage.ITEM);
        if (container == null) {
            return false;
        }
        long limit = (long) tank.getCapacity() * FluidTank.DROPLETS_PER_MB;
        if (drain) {
            return move(tank.getStorage(), container, limit);
        }
        if (move(container, tank.getStorage(), limit)) {
            return true;
        }
        return StorageUtil.findStoredResource(container) == null && move(tank.getStorage(), container, limit);
    }

    private static boolean move(Storage<FluidVariant> from, Storage<FluidVariant> to, long limit) {
        try (Transaction tx = Transaction.openOuter()) {
            long moved = StorageUtil.move(from, to, variant -> true, limit, tx);
            if (moved > 0) {
                tx.commit();
                return true;
            }
            return false;
        }
    }
}
