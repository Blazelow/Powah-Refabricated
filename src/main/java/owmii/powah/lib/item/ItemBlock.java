package owmii.powah.lib.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import owmii.powah.lib.block.AbstractBlock;

public class ItemBlock<B extends Block> extends BlockItem {
    private final B block;
    private final ResourceKey<CreativeModeTab> creativeTab;

    @SuppressWarnings("ConstantConditions")
    public ItemBlock(B block, Properties builder, @Nullable ResourceKey<CreativeModeTab> creativeTab) {
        super(block, builder);
        this.block = block;
        this.creativeTab = creativeTab;
    }

    public ResourceKey<CreativeModeTab> getCreativeTab() {
        return creativeTab;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (this.block instanceof AbstractBlock) {
            return ((AbstractBlock<?, ?>) this.block).getDisplayName(stack);
        }
        return super.getName(stack);
    }

    @Override
    public B getBlock() {
        return this.block;
    }
}
