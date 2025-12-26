package owmii.powah.block.reactor;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.block.Tiles;
import owmii.powah.lib.block.PowahBaseBlockEntity;
import owmii.powah.util.ValueIOUtil;

public class ReactorPartBlockEntity extends PowahBaseBlockEntity<Tier, ReactorBlock> {
    private BlockPos corePos = BlockPos.ZERO;
    private boolean extractor;
    private boolean built;

    // Cache capabilities of the core-tile to forward capability lookups to it more quickly
    @Nullable
    private BlockCapabilityCache<EnergyHandler, Direction> coreEnergyCache;
    @Nullable
    private BlockCapabilityCache<ResourceHandler<ItemResource>, Direction> coreItemCache;
    @Nullable
    private BlockCapabilityCache<ResourceHandler<FluidResource>, Direction> coreFluidCache;

    public ReactorPartBlockEntity(BlockPos pos, BlockState state, Tier variant) {
        super(Tiles.REACTOR_PART.get(), pos, state, variant);
    }

    public ReactorPartBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, Tier.STARTER);
    }

    @Override
    public void readSync(ValueInput input) {
        super.readSync(input);
        this.built = input.getBooleanOr("built", false);
        this.extractor = input.getBooleanOr("extractor", false);
        this.corePos = ValueIOUtil.readPos(input, "core_pos");
    }

    @Override
    public void writeSync(ValueOutput output) {
        output.putBoolean("built", this.built);
        output.putBoolean("extractor", this.extractor);
        ValueIOUtil.writePos(output, this.corePos, "core_pos");
        super.writeSync(output);
    }

    public void demolish(Level world) {
        BlockEntity tile = world.getBlockEntity(this.corePos);
        if (tile instanceof ReactorBlockEntity reactor) {
            reactor.demolish(world);
        }
    }

    @Nullable
    public EnergyHandler getCoreEnergyStorage() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (coreEnergyCache == null) {
                coreEnergyCache = BlockCapabilityCache.create(Capabilities.Energy.BLOCK, serverLevel, getCorePos(), null);
            }
            return coreEnergyCache.getCapability();
        } else {
            return level.getCapability(Capabilities.Energy.BLOCK, getCorePos(), null);
        }
    }

    @Nullable
    public ResourceHandler<ItemResource> getCoreItemHandler() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (coreItemCache == null) {
                coreItemCache = BlockCapabilityCache.create(Capabilities.Item.BLOCK, serverLevel, getCorePos(), null);
            }
            return coreItemCache.getCapability();
        } else {
            return level.getCapability(Capabilities.Item.BLOCK, getCorePos(), null);
        }
    }

    @Nullable
    public ResourceHandler<FluidResource> getCoreFluidHandler() {
        if (this.level instanceof ServerLevel serverLevel) {
            if (coreFluidCache == null) {
                coreFluidCache = BlockCapabilityCache.create(Capabilities.Fluid.BLOCK, serverLevel, getCorePos(), null);
            }
            return coreFluidCache.getCapability();
        } else {
            return level.getCapability(Capabilities.Fluid.BLOCK, getCorePos(), null);
        }
    }

    public Optional<ReactorBlockEntity> core() {
        if (this.level != null) {
            BlockEntity tile = this.level.getBlockEntity(this.corePos);
            if (tile instanceof ReactorBlockEntity reactorBlockEntity) {
                return Optional.of(reactorBlockEntity);
            }
        }
        return Optional.empty();
    }

    public BlockPos getCorePos() {
        return this.corePos;
    }

    public void setCorePos(BlockPos corePos) {
        if (!corePos.equals(this.corePos)) {
            this.corePos = corePos;
            invalidateCapabilities();
        }
    }

    public void setExtractor(boolean extractor) {
        if (extractor != this.extractor) {
            this.extractor = extractor;
            invalidateCapabilities();
        }
    }

    public boolean isExtractor() {
        return this.extractor;
    }

    public void setBuilt(boolean built) {
        this.built = built;
    }

    public boolean isBuilt() {
        return this.built;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        demolish(getLevel());
    }
}
