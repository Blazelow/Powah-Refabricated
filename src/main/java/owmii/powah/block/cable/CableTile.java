package owmii.powah.block.cable;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import team.reborn.energy.api.EnergyStorage;

import com.google.common.collect.Iterables;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;



import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.block.Tiles;
import owmii.powah.config.v2.types.CableConfig;
import owmii.powah.lib.block.AbstractEnergyStorage;
import owmii.powah.lib.block.IInventoryHolder;

public class CableTile extends AbstractEnergyStorage<CableConfig, CableBlock> implements IInventoryHolder {

    /**
     * Tag-Name used for synchronizing connected sides to the client.
     */
    private static final String NBT_ENERGY_SIDES = "cs";

    public final EnumSet<Direction> energySides = EnumSet.noneOf(Direction.class);
    @Nullable
    CableNet net = null;
    /**
     * True when energy is being inserted into the network.
     * Must be called after {@link #getCables()} to make sure that it is up-to-date for the network.
     */
    protected MutableBoolean netInsertionGuard = new MutableBoolean(false);
    protected int startIndex = 0;
    


    public CableTile(BlockPos pos, BlockState state, Tier variant) {
        super(Tiles.CABLE, pos, state, variant);
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        CableNet.addCable(this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        CableNet.removeCable(this);
    }

    public boolean isActive() {
        if (getLevel() instanceof ServerLevel serverLevel) {
            return serverLevel.getChunkSource().isPositionTicking(ChunkPos.asLong(getBlockPos()));
        }
        return false;
    }

    protected Iterable<CableTile> getCables() {
        if (net == null) {
            CableNet.calculateNetwork(this);
        }
        startIndex %= net.cableList.size();
        return Iterables.concat(net.cableList.subList(startIndex, net.cableList.size()), net.cableList.subList(0, startIndex));
    }

    @Override
    public void readSync(CompoundTag compound, HolderLookup.Provider registries) {
        super.readSync(compound, registries);
        readEnergySides(compound);
    }

    @Override
    public CompoundTag writeSync(CompoundTag compound, HolderLookup.Provider registries) {
        writeEnergySides(compound);

        return super.writeSync(compound, registries);
    }

    private void readEnergySides(CompoundTag compound) {
        // Read connected sides
        this.energySides.clear();
        var sideBits = compound.getByte(NBT_ENERGY_SIDES);
        for (var side : Direction.values()) {
            if ((sideBits & getSideMask(side)) != 0) {
                this.energySides.add(side);
            }
        }
    }

    private void writeEnergySides(CompoundTag compound) {
        // Write connected sides
        byte sideBits = 0;
        for (var side : this.energySides) {
            sideBits |= getSideMask(side);
        }
        compound.putByte(NBT_ENERGY_SIDES, sideBits);
    }

    private static byte getSideMask(Direction side) {
        return (byte) (1 << side.ordinal());
    }

    @Override
    protected long getEnergyCapacity() {
        return 0;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean keepStorable() {
        return false;
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate, @Nullable Direction direction) {
        if (this.level == null || direction == null || !checkRedstone() || !canReceiveEnergy(direction)) {
            return 0;
        }

        long received = 0;
        var cables = getCables();
        final var level = this.level;

        var insertionGuard = this.netInsertionGuard;
        if (insertionGuard.isTrue())
            return 0;
        insertionGuard.setTrue();

        try {
            if (!simulate) {
                startIndex++; // round robin!
            }

            for (var cable : cables) {
                long amount = maxReceive - received;
                if (amount <= 0)
                    break;
                if (!cable.energySides.isEmpty() && cable.isActive()) {
                    received += cable.pushEnergy(level, amount, simulate, direction, this);
                }
            }

            return received;
        } finally {
            insertionGuard.setFalse();
        }
    }

    private long pushEnergy(net.minecraft.world.level.Level level, long maxReceive, boolean simulate, @Nullable Direction direction, CableTile cable) {
        long received = 0;
        for (int i = 0; i < 6; ++i) {
            // Shift by tick count to ensure that it distributes evenly on average
            Direction side = Direction.from3DDataValue((i + level.getServer().getTickCount()) % 6);
            if (!this.energySides.contains(side))
                continue;

            long amount = Math.min(maxReceive - received, this.energy.getMaxExtract());
            if (amount <= 0)
                break;
            if (cable.equals(this) && side.equals(direction) || !canExtractEnergy(side))
                continue;
            BlockPos pos = this.worldPosition.relative(side);
            if (direction != null && cable.getBlockPos().relative(direction).equals(pos))
                continue;
            received += receive(level, pos, side.getOpposite(), amount, simulate);
        }
        return received;
    }

    private long receive(net.minecraft.world.level.Level level, BlockPos pos, Direction side, long amount, boolean simulate) {
        var storage = EnergyStorage.SIDED.find(level, pos, side);
        if (storage == null || !storage.supportsInsertion()) return 0;
        if (simulate) {
            try (Transaction tx = Transaction.openOuter()) {
                return storage.insert(amount, tx);
            }
        } else {
            try (Transaction tx = Transaction.openOuter()) {
                long inserted = storage.insert(amount, tx);
                tx.commit();
                return inserted;
            }
        }
    }

    public boolean canConnectTo(CableTile adjCable) {
        return variant == adjCable.variant;
    }
}
