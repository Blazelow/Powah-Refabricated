package owmii.powah.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import owmii.powah.block.reactor.ReactorPartTile;
import owmii.powah.item.ReactorItem;

public class ReactorItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {
    @Override
    public void render(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (stack.getItem() instanceof ReactorItem reactorItem) {
            var tile = new ReactorPartTile(BlockPos.ZERO, reactorItem.getBlock().defaultBlockState(), reactorItem.getVariant());
            var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            dispatcher.renderItem(tile, poseStack, buffer, packedLight, packedOverlay);
        }
    }
}
