package owmii.powah.block.solar;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.GeneratorConfig;
import owmii.powah.inventory.SolarMenu;
import owmii.powah.lib.block.PowahBaseBlockEntity;
import owmii.powah.lib.block.PowahBaseGeneratorBlock;
import owmii.powah.lib.item.EnergyBlockItem;
import owmii.powah.lib.logistics.inventory.BaseMenu;

public class SolarBlock extends PowahBaseGeneratorBlock<SolarBlock> implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;

    public SolarBlock(Properties properties, Tier variant) {
        super(properties, variant);
        setStateProps(state -> state.setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false));
    }

    @Override
    public EnergyBlockItem getBlockItem(Item.Properties properties, @Nullable ResourceKey<CreativeModeTab> group) {
        return super.getBlockItem(properties.stacksTo(1), group);
    }

    @Override
    public GeneratorConfig getConfig() {
        return Powah.config().generators.solar_panels;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SolarBlockEntity(pos, state, this.variant);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BaseMenu getContainer(int id, Inventory inventory, PowahBaseBlockEntity te, BlockHitResult result) {
        if (te instanceof SolarBlockEntity) {
            return new SolarMenu(id, inventory, (SolarBlockEntity) te);
        }
        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour,
            BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return createState(level, pos);
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return createState(context.getLevel(), context.getClickedPos());
    }

    private BlockState createState(LevelReader level, BlockPos pos) {
        final BlockState state = defaultBlockState();
        boolean north = canAttach(state, level, pos, Direction.NORTH);
        boolean south = canAttach(state, level, pos, Direction.SOUTH);
        boolean west = canAttach(state, level, pos, Direction.WEST);
        boolean east = canAttach(state, level, pos, Direction.EAST);
        return state.setValue(NORTH, !north).setValue(SOUTH, !south).setValue(WEST, !west).setValue(EAST, !east)
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
    }

    public boolean canAttach(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
        return level.getBlockState(pos.relative(direction)).getBlock() == this;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST);
        super.createBlockStateDefinition(builder);
    }

    public List<Direction> getConnectedSides(BlockState state) {
        List<Direction> list = new ArrayList<>();
        if (!state.getValue(NORTH))
            list.add(Direction.NORTH);
        if (!state.getValue(SOUTH))
            list.add(Direction.SOUTH);
        if (!state.getValue(WEST))
            list.add(Direction.WEST);
        if (!state.getValue(EAST))
            list.add(Direction.EAST);
        return list;
    }
}
