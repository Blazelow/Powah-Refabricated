package owmii.powah.block.solar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.block.Tiles;
import owmii.powah.item.Itms;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.PowahBaseGeneratorBlockEntity;
import owmii.powah.lib.logistics.energy.Energy;
import owmii.powah.util.Misc;

public class SolarBlockEntity extends PowahBaseGeneratorBlockEntity<SolarBlock> implements IInventoryHolder {
    public static final String CAN_SEE_SKY = "can_see_sky";
    public static final String HAS_LENS_OF_ENDER = "has_lens_of_ender";
    private boolean canSeeSky;
    private boolean hasLensOfEnder;

    public SolarBlockEntity(BlockPos pos, BlockState state, Tier variant) {
        super(Tiles.SOLAR_PANEL.get(), pos, state, variant);
        this.inv.add(1);
    }

    public SolarBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, Tier.STARTER);
    }

    @Override
    public void readSync(ValueInput input) {
        super.readSync(input);
        this.canSeeSky = input.getBooleanOr(CAN_SEE_SKY, false);
        this.hasLensOfEnder = input.getBooleanOr(HAS_LENS_OF_ENDER, false);
    }

    @Override
    public void writeSync(ValueOutput output) {
        output.putBoolean(CAN_SEE_SKY, this.canSeeSky);
        output.putBoolean(HAS_LENS_OF_ENDER, this.hasLensOfEnder);
        super.writeSync(output);
    }

    @Override
    protected int postTick(Level world) {
        if (isRemote())
            return -1;
        boolean flag = chargeItems(1) + extractFromSides(world) > 0;
        if (checkRedstone()) {
            if (!this.hasLensOfEnder && this.ticks % 40L == 0L) {
                boolean canSeeSkyNow = Misc.canBlockSeeSky(world, this.worldPosition.above());
                if (this.canSeeSky != canSeeSkyNow) {
                    this.canSeeSky = canSeeSkyNow;
                    sync();
                }
            }
            if (!this.energy.isFull()) {
                if ((this.canSeeSky || this.hasLensOfEnder) && (world.dimensionType().hasSkyLight() && world.getSkyDarken() < 4)) {
                    this.energy.produce(getGeneration());
                    flag = true;
                }
            }
        }
        return flag ? 5 : -1;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.hasLensOfEnder) {
            Block.popResource(getLevel(), this.worldPosition, new ItemStack(Itms.LENS_OF_ENDER.get()));
        }
    }

    public boolean canSeeSky() {
        return this.canSeeSky;
    }

    @Override
    public boolean keepEnergy() {
        return true;
    }

    @Override
    public boolean isEnergyPresent(@Nullable Direction side) {
        return Direction.DOWN.equals(side);
    }

    public boolean hasLensOfEnder() {
        return this.hasLensOfEnder;
    }

    public void setHasLensOfEnder(boolean hasLensOfEnder) {
        this.hasLensOfEnder = hasLensOfEnder;
        sync();
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack) {
        return Energy.chargeable(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack) {
        return true;
    }
}
