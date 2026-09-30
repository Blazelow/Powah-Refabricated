package owmii.powah.block.ender;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import owmii.powah.block.Tiles;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.IOwnable;

public class EnderCellBlockEntity extends PowahBaseEnderBlockEntity<EnderCellBlock> implements IOwnable, IInventoryHolder {
    public EnderCellBlockEntity(BlockPos pos, BlockState state) {
        super(Tiles.ENDER_CELL.get(), pos, state);
    }

    @Override
    protected int getInternalInventorySize() {
        return 3;
    }

    @Override
    public boolean isExtender() {
        return true;
    }
}
