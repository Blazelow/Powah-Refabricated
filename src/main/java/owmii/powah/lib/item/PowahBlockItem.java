package owmii.powah.lib.item;

import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;
import owmii.powah.lib.block.PowahBaseBlock;

public class PowahBlockItem<B extends Block> extends BlockItem {
    private final B block;
    private final ResourceKey<CreativeModeTab> creativeTab;

    @SuppressWarnings("ConstantConditions")
    public PowahBlockItem(B block, Properties builder, @Nullable ResourceKey<CreativeModeTab> creativeTab) {
        super(block, builder);
        this.block = block;
        this.creativeTab = creativeTab;
    }

    public ResourceKey<CreativeModeTab> getCreativeTab() {
        return creativeTab;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (this.block instanceof PowahBaseBlock<?, ?> baseBlock) {
            return baseBlock.getName();
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        if (this.block instanceof PowahBaseBlock<?, ?> baseBlock) {
            baseBlock.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        }
    }

    @Override
    public B getBlock() {
        return this.block;
    }
}
