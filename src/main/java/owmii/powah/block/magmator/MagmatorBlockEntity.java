package owmii.powah.block.magmator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import owmii.powah.api.PowahAPI;
import owmii.powah.block.Tiles;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.ITankHolder;
import owmii.powah.lib.block.PowahBaseGeneratorBlockEntity;
import owmii.powah.lib.logistics.energy.Energy;
import owmii.powah.lib.logistics.fluid.Tank;
import owmii.powah.util.ChargeUtil;
import owmii.powah.util.Util;

public class MagmatorBlockEntity extends PowahBaseGeneratorBlockEntity<MagmatorBlock> implements IInventoryHolder, ITankHolder {
    protected final Energy buffer = Energy.create(0);
    protected boolean burning;
    protected int burningTicks;

    public MagmatorBlockEntity(BlockPos pos, BlockState state) {
        super(Tiles.MAGMATOR.get(), pos, state);
        this.tank.setValidator(stack -> PowahAPI.getMagmaticFluidEnergyProduced(stack.getFluid()) != 0);
        this.tank.setChange(() -> sync(10));
    }

    @Override
    protected int getInternalInventorySize() {
        return 1;
    }

    @Override
    protected int getInternalTankCapacity() {
        return Util.bucketAmount() * 4;
    }

    @Override
    public void readSync(ValueInput input) {
        super.readSync(input);
        getEnergy().read(input, "energy_buffer", true, false);
        this.burning = input.getBooleanOr("burning", false);
    }

    @Override
    public void writeSync(ValueOutput output) {
        getEnergy().write(output, "energy_buffer", true, false);
        output.putBoolean("burning", this.burning);
        super.writeSync(output);
    }

    @Override
    protected int postTick(Level world) {
        if (!isRemote()) {
            if (checkRedstone()) {
                if (this.buffer.isEmpty() && !this.tank.isEmpty()) {
                    FluidStack fluid = this.tank.getFluid();
                    int energyProduced = PowahAPI.getMagmaticFluidEnergyProduced(fluid.getFluid());
                    if (energyProduced > 0) {
                        var amountPerDrain = 100;
                        var minStored = Math.min(this.tank.getFluidAmount(), amountPerDrain);
                        this.buffer.setStored((long) minStored * energyProduced / amountPerDrain);
                        this.buffer.setCapacity((long) minStored * energyProduced / amountPerDrain);

                        ResourceHandlerUtil.extractFirst(this.tank, _ -> true, minStored, null);
                    }
                }

                long min = Math.min(getEnergyGeneration(), this.buffer.getStored());
                if (min > 0 && getEnergy().getEmpty() >= min) {
                    getEnergy().produce(min);
                    this.buffer.consume(min);
                    burningTicks = 5; // show to be generating power for ~250ms
                    sync(4);
                }
            }

            var visiblyBurning = burningTicks-- > 0;
            if (this.burning != visiblyBurning) {
                this.burning = visiblyBurning;
                level.setBlock(getBlockPos(), getBlockState().setValue(BlockStateProperties.LIT, visiblyBurning), Block.UPDATE_ALL);
                sync(4);
            }
        }
        return chargeItems(1) + extractFromSides(world) > 0 ? 10 : -1;
    }

    public Tank getTank() {
        return this.tank;
    }

    @Override
    public boolean keepEnergy() {
        return true;
    }

    @Override
    public boolean keepFluid() {
        return true;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack) {
        return ChargeUtil.isChargeableItem(stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack) {
        return true;
    }

    public boolean isBurning() {
        return this.burning;
    }
}
