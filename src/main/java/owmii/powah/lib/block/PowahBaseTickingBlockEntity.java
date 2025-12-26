package owmii.powah.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import owmii.powah.lib.registry.IVariant;

public class PowahBaseTickingBlockEntity<V extends IVariant, B extends PowahBaseBlock<V, B>> extends PowahBaseBlockEntity<V, B> {
    public int ticks;
    private int syncTicks;
    private final SnapshotJournal<Integer> syncTicksJournal = new SnapshotJournal<>() {
        @Override
        protected Integer createSnapshot() {
            return syncTicks;
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            syncTicks = snapshot;
        }
    };

    public PowahBaseTickingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public PowahBaseTickingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, V variant) {
        super(type, pos, state, variant);
    }

    public void tick() {
        final Level world = this.level;
        if (world != null) {
            if (this.ticks == 0) {
                onFirstTick(world);
            }
            if (doPostTicks(world)) {
                int i = postTick(world);
                if (i > -1 && !isRemote()) {
                    sync(i);
                }
            }
            this.ticks++;
            if (!isRemote()) {
                if (this.syncTicks > -1)
                    this.syncTicks--;
                if (this.syncTicks == 0)
                    sync();
            } else {
                clientTick(world);
            }
        }
    }

    protected void onFirstTick(Level world) {
    }

    protected boolean doPostTicks(Level world) {
        return true;
    }

    protected int postTick(Level world) {
        return -1;
    }

    protected void clientTick(Level world) {
    }

    public void sync(int delay) {
        if (!isRemote()) {
            if (this.syncTicks <= 0 || delay < this.syncTicks) {
                this.syncTicks = delay;
            }
        }
    }

    public void sync(int delay, TransactionContext tx) {
        if (!isRemote()) {
            if (this.syncTicks <= 0 || delay < this.syncTicks) {
                syncTicksJournal.updateSnapshots(tx);
                this.syncTicks = delay;
            }
        }
    }
}
