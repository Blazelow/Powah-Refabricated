package owmii.powah.block.energizing;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.block.Tiles;
import owmii.powah.config.v2.types.EnergyConfig;
import owmii.powah.lib.block.AbstractEnergyStorageBlockEntity;
import owmii.powah.util.NBT;
import owmii.powah.util.Ticker;

public class EnergizingRodBlockEntity extends AbstractEnergyStorageBlockEntity<EnergyConfig, EnergizingRodBlock> {
    private BlockPos orbPos = BlockPos.ZERO;
    public final Ticker coolDown = new Ticker(20);

    public EnergizingRodBlockEntity(BlockPos pos, BlockState state, Tier variant) {
        super(Tiles.ENERGIZING_ROD.get(), pos, state, variant);
    }

    public EnergizingRodBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, Tier.STARTER);
    }

    @Override
    public void readSync(CompoundTag nbt, HolderLookup.Provider registries) {
        super.readSync(nbt, registries);
        this.orbPos = NBT.readPos(nbt, "OrbPos");
    }

    @Override
    public CompoundTag writeSync(CompoundTag nbt, HolderLookup.Provider registries) {
        NBT.writePos(nbt, this.orbPos, "OrbPos");
        return super.writeSync(nbt, registries);
    }

    @Override
    protected int postTick(Level world) {
        boolean flag = false;
        EnergizingOrbBlockEntity orb = getOrbTile();
        if (orb != null) {
            if (orb.containRecipe() && this.energy.hasEnergy()) {
                this.coolDown.onward();
                flag = true;
            } else if (this.coolDown.getTicks() > 0) {
                this.coolDown.back();
                flag = true;
            }

            if (this.coolDown.ended()) {
                long fill = Math.min(this.energy.getEnergyStored(), getBlock().getConfig().getTransfer(getVariant()));
                this.energy.consume(orb.fillEnergy(fill));
                flag = true;
            }
        }
        return flag ? 10 : -1;
    }

    @Nullable
    public EnergizingOrbBlockEntity getOrbTile() {
        if (this.level != null && this.orbPos != BlockPos.ZERO && level.isLoaded(this.orbPos)) {
            BlockEntity tile = this.level.getBlockEntity(this.orbPos);
            if (tile instanceof EnergizingOrbBlockEntity) {
                return (EnergizingOrbBlockEntity) tile;
            }
        }
        return null;
    }

    public boolean hasOrb() {
        return getOrbTile() != null;
    }

    public BlockPos getOrbPos() {
        return this.orbPos;
    }

    public void setOrbPos(BlockPos orbPos) {
        this.orbPos = orbPos;
        sync(2);
    }

    @Override
    public boolean keepEnergy() {
        return true;
    }

    @Override
    public boolean isEnergyPresent(@Nullable Direction side) {
        return side != null && side.equals(getBlockState().getValue(BlockStateProperties.FACING));
    }
}
