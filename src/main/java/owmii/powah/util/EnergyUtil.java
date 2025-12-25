package owmii.powah.util;

import com.google.common.primitives.Ints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public final class EnergyUtil {
    private EnergyUtil() {
    }

    public static boolean hasEnergy(Level level, BlockPos pos, Direction side) {
        return level.getCapability(Capabilities.Energy.BLOCK, pos, side) != null;
    }

    public static long pushEnergy(Level level, BlockPos pos, Direction side, long howMuch, TransactionContext tx) {
        var handler = level.getCapability(Capabilities.Energy.BLOCK, pos, side);
        return handler != null ? handler.insert(Ints.saturatedCast(howMuch), tx) : 0;
    }
}
