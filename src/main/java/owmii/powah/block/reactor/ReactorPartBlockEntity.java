package owmii.powah.block.reactor;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import team.reborn.energy.api.EnergyStorage;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.Tiles;
import owmii.powah.lib.block.PowahBaseBlockEntity;
import owmii.powah.util.ValueIOUtil;

public class ReactorPartBlockEntity extends PowahBaseBlockEntity<ReactorBlock> {
    private BlockPos corePos = BlockPos.ZERO;
    private boolean extractor;
    private boolean built;

    public ReactorPartBlockEntity(BlockPos pos, BlockState state) {
        super(Tiles.REACTOR_PART.get(), pos, state);
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
    public EnergyStorage getCoreEnergyStorage() {
        return findCore(EnergyStorage.SIDED);
    }

    @Nullable
    public Storage<ItemVariant> getCoreItemHandler() {
        return findCore(ItemStorage.SIDED);
    }

    @Nullable
    public Storage<FluidVariant> getCoreFluidHandler() {
        return findCore(FluidStorage.SIDED);
    }

    @Nullable
    private <A> A findCore(BlockApiLookup<A, Direction> lookup) {
        return this.level == null ? null : lookup.find(this.level, getCorePos(), null);
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
        this.corePos = corePos;
    }

    public void setExtractor(boolean extractor) {
        this.extractor = extractor;
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
