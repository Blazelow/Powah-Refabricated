package owmii.powah.lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import owmii.powah.lib.item.PowahBlockItem;
import owmii.powah.lib.registry.IVariant;
import owmii.powah.lib.registry.IVariantEntry;

public interface IBlock<V extends IVariant, B extends Block & IBlock<V, B>> extends IVariantEntry<V, B>, EntityBlock {
    @SuppressWarnings("unchecked")
    default PowahBlockItem<B> getBlockItem(Item.Properties properties, @Nullable ResourceKey<CreativeModeTab> group) {
        return new PowahBlockItem<>((B) this, properties, group);
    }

    @Nullable
    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Nullable
    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (newBlockEntity(BlockPos.ZERO, state) instanceof PowahBaseTickingBlockEntity<?, ?>) {
            return (l, p, s, be) -> ((PowahBaseTickingBlockEntity<?, ?>) be).tick();
        }
        return null;
    }
}
