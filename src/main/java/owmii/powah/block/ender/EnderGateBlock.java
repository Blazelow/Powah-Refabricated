package owmii.powah.block.ender;

import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.EnderConfig;
import owmii.powah.inventory.EnderCellContainer;
import owmii.powah.lib.block.PowahBaseBlockEntity;
import owmii.powah.lib.block.PowahBaseEnergyBlock;
import owmii.powah.lib.logistics.inventory.AbstractContainer;

public class EnderGateBlock extends PowahBaseEnergyBlock<EnderConfig, EnderGateBlock> {
    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.UP, box(6.0D, 15.5D, 6.0D, 10.0D, 16.0D, 10.0D),
            Direction.DOWN, box(6.0D, 0.0D, 6.0D, 10.0D, 0.5D, 10.0D),
            Direction.NORTH, box(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 0.5D),
            Direction.SOUTH, box(6.0D, 6.0D, 15.5D, 10.0D, 10.0D, 16.0D),
            Direction.EAST, box(15.5D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D),
            Direction.WEST, box(0.0D, 6.0D, 6.0D, 0.5D, 10.0D, 10.0D));

    public EnderGateBlock(Properties properties, Tier variant) {
        super(properties, variant);
        shapes.putAll(SHAPES);
    }

    @Override
    public EnderConfig getConfig() {
        return Powah.config().devices.ender_gates;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnderGateBlockEntity(pos, state, this.variant);
    }

    @Nullable
    @Override
    public AbstractContainer getContainer(int id, Inventory inventory, PowahBaseBlockEntity te, BlockHitResult result) {
        if (te instanceof EnderGateBlockEntity) {
            return new EnderCellContainer(id, inventory, (EnderGateBlockEntity) te);
        }
        return null;
    }

    @Override
    protected boolean checkValidEnergySideProperty() {
        return true;
    }

    @Override
    protected Facing getFacing() {
        return Facing.ALL;
    }
}
