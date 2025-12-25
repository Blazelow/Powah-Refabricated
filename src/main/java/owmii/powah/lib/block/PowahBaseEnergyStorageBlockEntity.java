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
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.config.IEnergyConfig;
import owmii.powah.lib.logistics.IRedstoneInteract;
import owmii.powah.lib.logistics.Transfer;
import owmii.powah.lib.logistics.energy.Energy;
import owmii.powah.lib.logistics.energy.SideConfig;
import owmii.powah.lib.registry.IVariant;
import owmii.powah.util.ChargeUtil;
import owmii.powah.util.Util;

public abstract class PowahBaseEnergyStorageBlockEntity<C extends IEnergyConfig<Tier>, B extends PowahBaseEnergyBlock<C, B>>
        extends PowahBaseTickingBlockEntity<Tier, B>
        implements IRedstoneInteract {
    protected final SideConfig sideConfig = new SideConfig(this);
    protected final Energy energy = Energy.create(0);
    private final @Nullable EnergyHandler[] externalAdapters = new EnergyHandler[Direction.values().length + 1];
    @SuppressWarnings("unchecked")
    private final BlockCapabilityCache<EnergyHandler, @Nullable Direction>[] capabilityCaches = new BlockCapabilityCache[6];

    public PowahBaseEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, IVariant.getEmpty());
    }

    public PowahBaseEnergyStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Tier variant) {
        super(type, pos, state, variant);
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
                if (canExtractEnergy(side)) {
                    if (capabilityCaches[side.ordinal()] == null) {
                        capabilityCaches[side.ordinal()] = BlockCapabilityCache.create(Capabilities.Energy.BLOCK, (ServerLevel) world,
                                worldPosition.relative(side), side.getOpposite());
                    }
                    long amount = Math.min(getEnergyTransfer(), getEnergy().getStored());
                    var cap = capabilityCaches[side.ordinal()].getCapability();
                    long toExtract;
                    try (var tx = Transaction.openRoot()) {
                        toExtract = cap == null ? 0 : cap.insert(Ints.saturatedCast(amount), tx);
                        extracted += extractEnergy(Util.safeInt(toExtract), tx, side);
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
        long charged = ChargeUtil.chargeItemsInInventory(inv, i, j, getEnergyTransfer(), energy.getStored());
        energy.consume(charged);
        return charged;
    }

    public long extractEnergy(long maxExtract, TransactionContext tx, @Nullable Direction side) {
        if (!canExtractEnergy(side))
            return 0;
        final Energy energy = getEnergy();
        long extracted = Math.min(energy.getStored(), Math.min(energy.getMaxExtract(), maxExtract));
        // TODO 26.1: if (!simulate && extracted > 0) {
        // TODO 26.1: energy.consume(extracted);
        // TODO 26.1: sync(10);
        // TODO 26.1: }
        return extracted;
    }

    public long insertEnergy(long maxReceive, TransactionContext tx, @Nullable Direction side) {
        if (!canReceiveEnergy(side))
            return 0;
        final Energy energy = getEnergy();
        long received = Math.min(energy.getEmpty(), Math.min(energy.getMaxReceive(), maxReceive));
        // TODO 26.1 if (!simulate && received > 0) {
        // TODO 26.1 energy.produce(received);
        // TODO 26.1 sync(10);
        // TODO 26.1 }
        return received;
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

    protected long getEnergyCapacity() {
        return getConfig().getCapacity(getVariant());
    }

    protected long getEnergyTransfer() {
        return getConfig().getTransfer(getVariant());
    }

    protected C getConfig() {
        return getBlock().getConfig();
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
    public EnergyHandler getExternalStorage(@Nullable Direction side) {
        if (!isEnergyPresent(side)) {
            return null;
        }

        int index = side != null ? side.ordinal() : Direction.values().length;
        if (externalAdapters[index] == null) {
            externalAdapters[index] = new ExternalAdapter(side);
        }

        return externalAdapters[index];
    }

    private final class ExternalAdapter implements EnergyHandler {
        private final @Nullable Direction side;

        public ExternalAdapter(@Nullable Direction side) {
            this.side = side;
        }

        @Override
        public long getAmountAsLong() {
            return getEnergy().getStored();
        }

        @Override
        public long getCapacityAsLong() {
            return getEnergy().getMaxEnergyStored();
        }

        @Override
        public int insert(int amount, TransactionContext tx) {
            return Util.safeInt(PowahBaseEnergyStorageBlockEntity.this.insertEnergy(amount, tx, side));
        }

        @Override
        public int extract(int amount, TransactionContext tx) {
            return Util.safeInt(PowahBaseEnergyStorageBlockEntity.this.extractEnergy(amount, tx, side));
        }
    }

}
