package owmii.powah.block.ender;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;
import owmii.powah.api.energy.endernetwork.IEnderExtender;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.EnderConfig;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.IOwnable;
import owmii.powah.lib.block.PowahBaseEnergyBlock;
import owmii.powah.lib.block.PowahBaseEnergyStorageBlockEntity;
import owmii.powah.lib.logistics.energy.Energy;
import owmii.powah.util.Player;
import owmii.powah.util.math.RangedInt;

public class PowahBaseEnderBlockEntity<B extends PowahBaseEnergyBlock<EnderConfig, B>> extends PowahBaseEnergyStorageBlockEntity<EnderConfig, B>
        implements IOwnable, IInventoryHolder {
    private final RangedInt channel = new RangedInt(12);

    @Nullable
    private GameProfile owner;
    private boolean flag;

    public PowahBaseEnderBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Tier variant) {
        super(type, pos, state, variant);
    }

    @Override
    public void readStorable(ValueInput input) {
        super.readStorable(input);
        this.channel.read(input, "channel");
        this.owner = input.read("owner", ExtraCodecs.STORED_GAME_PROFILE.codec()).orElse(null);
    }

    @Override
    public void writeStorable(ValueOutput output) {
        this.channel.write(output, "channel");
        output.storeNullable("owner", ExtraCodecs.STORED_GAME_PROFILE.codec(), owner);
        super.writeStorable(output);
    }

    @Override
    protected void onFirstTick(Level world) {
        super.onFirstTick(world);
        getEnergy().setTransfer(getEnergyTransfer());
    }

    @Override
    protected int postTick(Level world) {
        if (!isRemote()) {
            if (this.energy.clone(getEnergy())) {
                sync(5);
            }
        }
        return chargeItems(1, 3) + extractFromSides(world) > 0 ? 10 : -1;
    }

    @Override
    public void onSlotChanged(int slot) {
        if (this.level != null && slot == 0) {
            ItemStack stack = this.inv.getStackInSlot(0);
            if (isExtender() && stack.getItem() instanceof IEnderExtender e) {
                Energy energy = getEnergy();
                long cap = e.getExtendedCapacity(stack);
                long newCap = energy.getCapacity() + cap;
                if (cap <= Energy.MAX && newCap > 0 && newCap <= Energy.MAX) {
                    if (!isRemote()) {
                        energy.setCapacity(newCap);
                        energy.setStored(e.getExtendedEnergy(stack) + getEnergy().getStored());
                        setEnergy(energy);
                    }
                    stack.shrink(1);
                    this.level.playSound(null, this.worldPosition, SoundEvents.ENDER_EYE_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public long insertEnergy(long maxReceive, TransactionContext tx, @Nullable Direction side) {
        final long l = super.insertEnergy(maxReceive, tx, side);
        setEnergy(getEnergy());
        return l;
    }

    @Override
    public long extractEnergy(long maxExtract, TransactionContext tx, @Nullable Direction side) {
        final long l = super.extractEnergy(maxExtract, tx, side);
        setEnergy(getEnergy());
        return l;
    }

    @Override
    public boolean canExtractEnergy(@Nullable Direction side) {
        return checkRedstone() && super.canExtractEnergy(side);
    }

    @Override
    public boolean canReceiveEnergy(@Nullable Direction side) {
        return checkRedstone() && super.canReceiveEnergy(side);
    }

    @Override
    public void onPlaced(Level world, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, state, placer, stack);
        if (getOwner() == null && placer instanceof ServerPlayer && !Player.isFake((net.minecraft.world.entity.player.Player) placer)) {
            setOwner(((ServerPlayer) placer).getGameProfile());
        }
    }

    public void setEnergy(Energy energy) {
        if (level instanceof ServerLevel serverLevel && this.owner != null) {
            EnderNetwork network = EnderNetwork.get(serverLevel);
            network.setEnergy(this.owner.id(), this.channel.get(), energy);
        }
    }

    public boolean isExtender() {
        return true;
    }

    @Nullable
    @Override
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(@Nullable GameProfile owner) {
        this.owner = owner;
    }

    @Override
    public Energy getEnergy() {
        if (level instanceof ServerLevel serverLevel) {
            return EnderNetwork.get(serverLevel).getEnergy(this, this.channel.get()).setTransfer(getEnergyTransfer());
        } else {
            return this.energy;
        }
    }

    public RangedInt getChannel() {
        return this.channel;
    }

    public int getMaxChannels() {
        return getConfig().channels.get(getVariant());
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack) {
        if (slot == 0) {
            if (stack.getItem() instanceof IEnderExtender extender) {
                long l = extender.getExtendedCapacity(stack);
                return l > 0 && l + getEnergy().getCapacity() <= Energy.MAX;
            } else
                return false;
        }
        return Energy.chargeable(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack) {
        return true;
    }
}
