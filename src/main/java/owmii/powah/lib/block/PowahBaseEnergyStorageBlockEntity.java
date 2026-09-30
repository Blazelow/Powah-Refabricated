package owmii.powah.lib.block;

import com.google.common.primitives.Ints;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.lib.logistics.IRedstoneInteract;
import owmii.powah.lib.logistics.Transfer;
import owmii.powah.lib.logistics.energy.Energy;
import owmii.powah.lib.logistics.energy.SideConfig;
import owmii.powah.util.ChargeUtil;
import owmii.powah.util.EnergyUtil;
import team.reborn.energy.api.EnergyStorage;
import owmii.powah.util.Util;

public abstract class PowahBaseEnergyStorageBlockEntity<B extends PowahBaseEnergyBlock<B>>
        extends PowahBaseTickingBlockEntity<B>
        implements IRedstoneInteract {
    protected final SideConfig sideConfig = new SideConfig(this);
    private final Energy energy = Energy.create(0);
    private final @Nullable EnergyStorage[] externalAdapters = new EnergyStorage[Direction.values().length + 1];
    @SuppressWarnings("unchecked")
    private final EnergyUtil.Target[] capabilityCaches = new EnergyUtil.Target[6];

    public PowahBaseEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void readSync(ValueInput input) {
        this.sideConfig.read(input);
        if (!keepEnergy()) {
            this.energy.read(input, true, false);
        }
        super.readSync(input);
    }

    @Override
    public void writeSync(ValueOutput output) {
        this.sideConfig.write(output);
        if (!keepEnergy()) {
            this.energy.write(output, true, false);
        }
        super.writeSync(output);
    }

    @Override
    public void readStorable(ValueInput input) {
        if (keepEnergy()) {
            this.energy.read(input, false, false);
        }
        super.readStorable(input);
    }

    @Override
    public void writeStorable(ValueOutput output) {
        if (keepEnergy()) {
            this.energy.write(output, false, false);
        }
        super.writeStorable(output);
    }

    public boolean keepEnergy() {
        return false;
    }

    @Override
    protected void onFirstTick(Level world) {
        super.onFirstTick(world);
        this.energy.setCapacity(getEnergyCapacity());
        this.energy.setTransfer(getEnergyTransfer());
        getSideConfig().init();
        sync();
    }

    protected long extractFromSides(Level world) {
        long extracted = 0;
        if (!isRemote()) {
            for (Direction side : Direction.values()) {
                long amount = Math.min(getEnergyTransfer(), getEnergy().getStored());
                if (amount == 0) {
                    return extracted;
                }

                if (canExtractEnergy(side)) {
                    if (capabilityCaches[side.ordinal()] == null) {
                        capabilityCaches[side.ordinal()] = new EnergyUtil.Target((ServerLevel) world, worldPosition.relative(side),
                                side.getOpposite());
                    }
                    var cap = capabilityCaches[side.ordinal()].find();
                    if (cap == null) {
                        continue;
                    }
                    try (var tx = Transaction.openOuter()) {
                        var toExtract = EnergyUtil.insert(cap, amount, tx);
                        if (toExtract > 0) {
                            extracted += extractEnergy(Util.safeInt(toExtract), tx, side);
                        }
                        tx.commit();
                    }
                }
            }
        }
        return extracted;
    }

    protected long chargeItems(int i) {
        return chargeItems(0, i);
    }

    protected long chargeItems(int i, int j) {
        final Energy energy = getEnergy();
        try (var tx = Transaction.openOuter()) {
            long charged = ChargeUtil.chargeItemsInInventory(inv, i, j, getEnergyTransfer(), energy.getStored(), tx);
            energy.extractEnergy(charged, tx);
            tx.commit();
            return charged;
        }
    }

    public long extractEnergy(long maxExtract, TransactionContext tx, @Nullable Direction side) {
        if (!canExtractEnergy(side))
            return 0;
        var extracted = getEnergy().extractEnergy(maxExtract, tx);
        if (extracted > 0) {
            sync(10, tx);
        }
        return extracted;
    }

    public long insertEnergy(long maxReceive, TransactionContext tx, @Nullable Direction side) {
        if (!canReceiveEnergy(side))
            return 0;
        var result = getEnergy().insertEnergy(maxReceive, tx);
        if (result > 0) {
            sync(10, tx);
        }
        return result;
    }

    public boolean canExtractEnergy(@Nullable Direction side) {
        return side == null || isEnergyPresent(side) && this.sideConfig.getType(side).canExtract;
    }

    public boolean canReceiveEnergy(@Nullable Direction side) {
        return side == null || isEnergyPresent(side) && this.sideConfig.getType(side).canReceive;
    }

    public boolean isEnergyPresent(@Nullable Direction side) {
        return true;
    }

    @Override
    public void onAdded(Level world, BlockState state, BlockState oldState, boolean isMoving) {
        super.onAdded(world, state, oldState, isMoving);
        if (state.getBlock() != oldState.getBlock()) {
            getSideConfig().init();
        }
    }

    protected final long getEnergyCapacity() {
        return getBlock().getEnergyCapacity();
    }

    protected final long getEnergyTransfer() {
        return getBlock().getEnergyTransfer();
    }

    public Energy getEnergy() {
        return this.energy;
    }

    public Transfer getTransferType() {
        return Transfer.ALL;
    }

    public SideConfig getSideConfig() {
        return this.sideConfig;
    }

    @Nullable
    public EnergyStorage getExternalStorage(@Nullable Direction side) {
        if (side != null && !isEnergyPresent(side)) {
            return null;
        }

        int index = side != null ? side.ordinal() : Direction.values().length;
        if (externalAdapters[index] == null) {
            externalAdapters[index] = new ExternalAdapter(side);
        }

        return externalAdapters[index];
    }

    public Tier getTier() {
        return getBlock().getTier();
    }

    private final class ExternalAdapter implements EnergyStorage {
        private final @Nullable Direction side;

        public ExternalAdapter(@Nullable Direction side) {
            this.side = side;
        }

        @Override
        public long getAmount() {
            return getEnergy().getStored();
        }

        @Override
        public long getCapacity() {
            return getEnergy().getMaxEnergyStored();
        }

        @Override
        public long insert(long amount, TransactionContext tx) {
            return PowahBaseEnergyStorageBlockEntity.this.insertEnergy(amount, tx, side);
        }

        @Override
        public long extract(long amount, TransactionContext tx) {
            return PowahBaseEnergyStorageBlockEntity.this.extractEnergy(amount, tx, side);
        }

        @Override
        public boolean supportsInsertion() {
            return canReceiveEnergy(side);
        }

        @Override
        public boolean supportsExtraction() {
            return canExtractEnergy(side);
        }
    }

}
