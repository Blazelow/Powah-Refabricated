package owmii.powah.lib.block;

import com.google.common.primitives.Ints;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.config.IEnergyConfig;
import owmii.powah.lib.logistics.IRedstoneInteract;
import owmii.powah.lib.logistics.Transfer;
import owmii.powah.lib.logistics.energy.Energy;
import owmii.powah.lib.logistics.energy.SideConfig;
import owmii.powah.lib.registry.IVariant;
import owmii.powah.util.ChargeUtil;
import owmii.powah.util.EnergyUtil;
import owmii.powah.util.Util;
import team.reborn.energy.api.EnergyStorage;

public abstract class AbstractEnergyStorage<C extends IEnergyConfig<Tier>, B extends AbstractEnergyBlock<C, B>> extends AbstractTickableTile<Tier, B>
        implements IRedstoneInteract {
    protected final SideConfig sideConfig = new SideConfig(this);
    protected final Energy energy = Energy.create(0);
    // Per-side external adapters (lazy init)
    private final EnergyStorage[] externalAdapters = new EnergyStorage[Direction.values().length + 1];

    public AbstractEnergyStorage(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        this(type, pos, state, IVariant.getEmpty());
    }

    public AbstractEnergyStorage(BlockEntityType<?> type, BlockPos pos, BlockState state, Tier variant) {
        super(type, pos, state, variant);
    }

    @Override
    public void readSync(CompoundTag nbt, HolderLookup.Provider registries) {
        this.sideConfig.read(nbt);
        if (!keepEnergy()) {
            this.energy.read(nbt, true, false);
        }
        super.readSync(nbt, registries);
    }

    @Override
    public CompoundTag writeSync(CompoundTag nbt, HolderLookup.Provider registries) {
        this.sideConfig.write(nbt);
        if (!keepEnergy()) {
            this.energy.write(nbt, true, false);
        }
        return super.writeSync(nbt, registries);
    }

    @Override
    public void readStorable(CompoundTag nbt, HolderLookup.Provider registries) {
        if (keepEnergy()) {
            this.energy.read(nbt, false, false);
        }
        super.readStorable(nbt, registries);
    }

    @Override
    public CompoundTag writeStorable(CompoundTag nbt, HolderLookup.Provider registries) {
        if (keepEnergy()) {
            this.energy.write(nbt, false, false);
        }
        return super.writeStorable(nbt, registries);
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
                    long amount = Math.min(getEnergyTransfer(), getEnergy().getStored());
                    long pushed = EnergyUtil.pushEnergy(world, worldPosition.relative(side), side.getOpposite(), amount);
                    extracted += extractEnergy(Util.safeInt(pushed), false, side);
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

    public long extractEnergy(long maxExtract, boolean simulate, @Nullable Direction side) {
        if (!canExtractEnergy(side))
            return 0;
        final Energy energy = getEnergy();
        long extracted = Math.min(energy.getStored(), Math.min(energy.getMaxExtract(), maxExtract));
        if (!simulate && extracted > 0) {
            energy.consume(extracted);
            sync(10);
        }
        return extracted;
    }

    public long receiveEnergy(long maxReceive, boolean simulate, @Nullable Direction side) {
        if (!canReceiveEnergy(side))
            return 0;
        final Energy energy = getEnergy();
        long received = Math.min(energy.getEmpty(), Math.min(energy.getMaxReceive(), maxReceive));
        if (!simulate && received > 0) {
            energy.produce(received);
            sync(10);
        }
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

    /**
     * Returns a TR Energy EnergyStorage for external use on the given side.
     * Called during capability registration in Powah.java.
     */
    @Nullable
    public EnergyStorage getExternalStorage(@Nullable Direction side) {
        if (!isEnergyPresent(side)) {
            return null;
        }
        int index = side != null ? side.ordinal() : Direction.values().length;
        if (externalAdapters[index] == null) {
            externalAdapters[index] = new ExternalAdapter(side);
        }
        return externalAdapters[index];
    }

    private final class ExternalAdapter implements EnergyStorage {
        private final @Nullable Direction side;

        public ExternalAdapter(@Nullable Direction side) {
            this.side = side;
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            long simulated = AbstractEnergyStorage.this.receiveEnergy(maxAmount, true, side);
            if (simulated > 0) {
                transaction.addCloseCallback((t, result) -> {
                    if (result.wasCommitted()) {
                        AbstractEnergyStorage.this.receiveEnergy(maxAmount, false, side);
                    }
                });
            }
            return simulated;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            long simulated = AbstractEnergyStorage.this.extractEnergy(maxAmount, true, side);
            if (simulated > 0) {
                transaction.addCloseCallback((t, result) -> {
                    if (result.wasCommitted()) {
                        AbstractEnergyStorage.this.extractEnergy(maxAmount, false, side);
                    }
                });
            }
            return simulated;
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
        public boolean supportsInsertion() {
            return AbstractEnergyStorage.this.canReceiveEnergy(side);
        }

        @Override
        public boolean supportsExtraction() {
            return AbstractEnergyStorage.this.canExtractEnergy(side);
        }
    }
}
