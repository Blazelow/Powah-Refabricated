package owmii.powah.util;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import team.reborn.energy.api.EnergyStorage;

public final class EnergyUtil {
    private EnergyUtil() {
    }

    public static boolean hasEnergy(Level level, BlockPos pos, Direction side) {
        return EnergyStorage.SIDED.find(level, pos, side) != null;
    }

    public static long pushEnergy(Level level, BlockPos pos, Direction side, long howMuch) {
        var storage = EnergyStorage.SIDED.find(level, pos, side);
        if (storage == null || !storage.supportsInsertion()) return 0;
        try (Transaction tx = Transaction.openOuter()) {
            long inserted = storage.insert(howMuch, tx);
            tx.commit();
            return inserted;
        }
    }
}
