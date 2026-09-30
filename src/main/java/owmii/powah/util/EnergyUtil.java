package owmii.powah.util;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

public final class EnergyUtil {
    private EnergyUtil() {
    }

    public static boolean hasEnergy(Level level, BlockPos pos, Direction side) {
        return EnergyStorage.SIDED.find(level, pos, side) != null;
    }

    public static long pushEnergy(Level level, BlockPos pos, Direction side, long howMuch, TransactionContext tx) {
        return insert(EnergyStorage.SIDED.find(level, pos, side), howMuch, tx);
    }

    public static long insert(@Nullable EnergyStorage storage, long amount, TransactionContext tx) {
        if (storage == null || amount <= 0 || !storage.supportsInsertion()) {
            return 0;
        }
        return storage.insert(amount, tx);
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

        public long insert(long amount, TransactionContext tx) {
            return EnergyUtil.insert(find(), amount, tx);
        }
    }
}
