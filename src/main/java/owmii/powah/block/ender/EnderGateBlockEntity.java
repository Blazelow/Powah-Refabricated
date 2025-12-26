package owmii.powah.block.ender;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.Tier;
import owmii.powah.block.Tiles;

public class EnderGateBlockEntity extends PowahBaseEnderBlockEntity<EnderGateBlock> {
    public EnderGateBlockEntity(BlockPos pos, BlockState state, Tier variant) {
        super(Tiles.ENDER_GATE.get(), pos, state, variant);
    }

    public EnderGateBlockEntity(BlockPos pos, BlockState state) {
        this(pos, state, Tier.STARTER);
    }

    @Override
    protected int getInternalInventorySize() {
        return 3;
    }

    @Override
    public boolean isExtender() {
        return false;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack) {
        return slot > 0 && super.canInsert(slot, stack);
    }

    @Override
    public boolean isEnergyPresent(@Nullable Direction side) {
        return side != null && side.equals(getBlockState().getValue(BlockStateProperties.FACING));
    }
}
