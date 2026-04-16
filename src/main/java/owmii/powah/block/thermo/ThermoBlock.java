package owmii.powah.block.thermo;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.GeneratorConfig;
import owmii.powah.inventory.ThermoContainer;
import owmii.powah.lib.block.AbstractGeneratorBlock;
import owmii.powah.lib.block.AbstractTileEntity;
import owmii.powah.lib.item.EnergyBlockItem;
import owmii.powah.lib.logistics.fluid.Tank;
import owmii.powah.lib.logistics.inventory.AbstractContainer;

public class ThermoBlock extends AbstractGeneratorBlock<ThermoBlock> {
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
        return new ThermoTile(pos, state, this.variant);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack pStack, BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
            BlockHitResult pHitResult) {
        BlockEntity tile = pLevel.getBlockEntity(pPos);
        if (tile instanceof ThermoTile genTile) {
            Tank tank = genTile.getTank();
            {
            try (var transaction = net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter()) {
                var ctx = net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext.ofPlayerHand(pPlayer, pHand);
                var itemFluid = net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.ITEM.find(pPlayer.getItemInHand(pHand), ctx);
                if (itemFluid != null) {
                    long moved = net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.move(
                            itemFluid, tank.asFabricStorage(), f -> true, Long.MAX_VALUE, transaction);
                    if (moved == 0) {
                        moved = net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.move(
                                tank.asFabricStorage(), itemFluid, f -> true, Long.MAX_VALUE, transaction);
                    }
                    if (moved > 0) {
                        transaction.commit();
                        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
                    }
                }
            }
        }
        return super.useItemOn(pStack, pState, pLevel, pPos, pPlayer, pHand, pHitResult);
    }

    @Nullable
    @Override
    public AbstractContainer getContainer(int id, Inventory inventory, AbstractTileEntity te, BlockHitResult result) {
        if (te instanceof ThermoTile) {
            return new ThermoContainer(id, inventory, (ThermoTile) te);
        }
        return null;
    }
}
