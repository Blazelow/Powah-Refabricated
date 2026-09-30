package owmii.powah.util;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import owmii.powah.lib.logistics.energy.BlockEnergyStorage;
import team.reborn.energy.api.EnergyStorage;

public final class EnergyUtil {
    private EnergyUtil() {
    }

    public static boolean hasEnergy(Level level, BlockPos pos, Direction side) {
        return EnergyStorage.SIDED.find(level, pos, side) != null;
    }

    public static long pushEnergy(Level level, BlockPos pos, Direction side, long howMuch) {
        return insert(EnergyStorage.SIDED.find(level, pos, side), howMuch, false);
    }

    public static long insert(@Nullable EnergyStorage storage, long amount, boolean simulate) {
        if (storage == null || amount <= 0) {
            return 0;
        }
        if (storage instanceof BlockEnergyStorage own) {
            return own.insertNative(amount, simulate);
        }
        if (!storage.supportsInsertion()) {
            return 0;
        }
        Transaction tx = openTransaction();
        if (tx == null) {
            return 0;
        }
        try (tx) {
            long inserted = storage.insert(amount, tx);
            if (!simulate) {
                tx.commit();
            }
            return inserted;
        }
    }

    @Nullable
    private static Transaction openTransaction() {
        try {
            var current = Transaction.getCurrentUnsafe();
            return current == null ? Transaction.openOuter() : current.openNested();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public static final class Target {
        private final BlockApiCache<EnergyStorage, Direction> cache;
        private final Direction side;

        public Target(ServerLevel level, BlockPos pos, Direction side) {
            this.cache = BlockApiCache.create(EnergyStorage.SIDED, level, pos);
            this.side = side;
        }

        @Nullable
        public EnergyStorage find() {
            return cache.find(side);
        }

        public long receive(long amount, boolean simulate) {
            return insert(find(), amount, simulate);
        }
    }
}
