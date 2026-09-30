package owmii.powah.lib.logistics.fluid;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public final class FluidUtil {
    private FluidUtil() {
    }

    public static boolean interactWithFluidHandler(Player player, InteractionHand hand, BlockPos pos, Tank tank) {
        return FluidStorageUtil.interactWithFluidStorage(tank.getStorage(), player, hand);
    }

    public static boolean interactWithCarried(Player player, AbstractContainerMenu menu, Tank tank, boolean drain) {
        var context = ContainerItemContext.ofPlayerCursor(player, menu);
        Storage<FluidVariant> container = context.find(FluidStorage.ITEM);
        if (container == null) {
            return false;
        }
        long limit = (long) tank.getCapacity() * Tank.DROPLETS_PER_MB;
        if (drain) {
            return move(tank.getStorage(), container, limit, player, true);
        }
        if (move(container, tank.getStorage(), limit, player, false)) {
            return true;
        }
        return StorageUtil.findStoredResource(container) == null && move(tank.getStorage(), container, limit, player, true);
    }

    private static boolean move(Storage<FluidVariant> from, Storage<FluidVariant> to, long limit, Player player, boolean pickup) {
        FluidVariant moved;
        try (Transaction tx = Transaction.openOuter()) {
            moved = StorageUtil.findExtractableResource(from, tx);
            if (moved == null || StorageUtil.move(from, to, variant -> true, limit, tx) <= 0) {
                return false;
            }
            tx.commit();
        }
        playSoundAndGameEvent(moved, player.level(), player, pickup);
        return true;
    }

    private static void playSoundAndGameEvent(FluidVariant fluid, Level level, Player player, boolean pickup) {
        var sound = pickup ? FluidVariantAttributes.getFillSound(fluid) : FluidVariantAttributes.getEmptySound(fluid);
        level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, pickup ? GameEvent.FLUID_PICKUP : GameEvent.FLUID_PLACE, player.position().add(0, 0.5, 0));
    }
}
