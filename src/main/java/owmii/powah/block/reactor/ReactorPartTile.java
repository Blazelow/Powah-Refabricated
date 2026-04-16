package owmii.powah.block.reactor;

import java.util.Optional;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.block.Tiles;
import owmii.powah.lib.block.AbstractTileEntity;
import owmii.powah.util.NBT;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class ReactorPartTile extends AbstractTileEntity<Tier, ReactorBlock> {
    private BlockPos corePos = BlockPos.ZERO;
    private boolean extractor;
    private boolean built;

    public ReactorPartTile(BlockPos pos, BlockState state, Tier variant) {
        super(Tiles.REACTOR_PART, pos, state, variant);
    }

    public ReactorPartTile(BlockPos pos, BlockState state) {
        this(pos, state, Tier.STARTER);
    }

    @Override
    public void readSync(CompoundTag compound, HolderLookup.Provider registries) {
        super.readSync(compound, registries);
        this.built = compound.getBoolean("built");
        this.extractor = compound.getBoolean("extractor");
        this.corePos = NBT.readPos(compound, "core_pos");
    }

    @Override
    public CompoundTag writeSync(CompoundTag compound, HolderLookup.Provider registries) {
        compound.putBoolean("built", this.built);
        compound.putBoolean("extractor", this.extractor);
        NBT.writePos(compound, this.corePos, "core_pos");
        return super.writeSync(compound, registries);
    }

    public void demolish(net.minecraft.world.level.Level world) {
        BlockEntity tile = world.getBlockEntity(this.corePos);
        if (tile instanceof ReactorTile reactor) {
            reactor.demolish(world);
        }
    }

    @Nullable
    public EnergyStorage getCoreEnergyStorage() {
        if (this.level == null) return null;
        return EnergyStorage.SIDED.find(this.level, getCorePos(), null);
    }

    @Nullable
    public Storage<ItemVariant> getCoreItemHandler() {
        if (this.level == null) return null;
        return ItemStorage.SIDED.find(this.level, getCorePos(), null);
    }

    @Nullable
    public Storage<FluidVariant> getCoreFluidHandler() {
        if (this.level == null) return null;
        return FluidStorage.SIDED.find(this.level, getCorePos(), null);
    }

    public Optional<ReactorTile> core() {
        if (this.level != null) {
            BlockEntity tile = this.level.getBlockEntity(this.corePos);
            if (tile instanceof ReactorTile reactorTile) {
                return Optional.of(reactorTile);
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
}
