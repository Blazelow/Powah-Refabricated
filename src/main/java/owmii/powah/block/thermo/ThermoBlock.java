package owmii.powah.block.thermo;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jspecify.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.GeneratorConfig;
import owmii.powah.inventory.ThermoContainer;
import owmii.powah.lib.block.PowahBaseGeneratorBlock;
import owmii.powah.lib.block.PowahBaseBlockEntity;
import owmii.powah.lib.item.EnergyBlockItem;
import owmii.powah.lib.logistics.fluid.Tank;
import owmii.powah.lib.logistics.inventory.AbstractContainer;

public class ThermoBlock extends PowahBaseGeneratorBlock<ThermoBlock> {
    public ThermoBlock(Properties properties, Tier variant) {
        super(properties, variant);
    }

    @Override
    public GeneratorConfig getConfig() {
        return Powah.config().generators.thermo_generators;
    }

    @Override
    public EnergyBlockItem getBlockItem(Item.Properties properties, @Nullable ResourceKey<CreativeModeTab> group) {
        return super.getBlockItem(properties.stacksTo(1), group);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThermoBlockEntity(pos, state, this.variant);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHitResult) {
        BlockEntity tile = pLevel.getBlockEntity(pPos);
        if (tile instanceof ThermoBlockEntity genTile) {
            Tank tank = genTile.getTank();
            if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, tank)) {
                genTile.sync();
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Nullable
    @Override
    public AbstractContainer getContainer(int id, Inventory inventory, PowahBaseBlockEntity te, BlockHitResult result) {
        if (te instanceof ThermoBlockEntity) {
            return new ThermoContainer(id, inventory, (ThermoBlockEntity) te);
        }
        return null;
    }
}
