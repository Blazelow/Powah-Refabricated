package owmii.powah.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import owmii.powah.block.cable.CableBlockEntity;
import owmii.powah.client.model.CableModel;
import owmii.powah.client.model.PowahLayerDefinitions;

public class CableRenderer implements BlockEntityRenderer<CableBlockEntity, CableRendererState> {
    private final CableModel model;

    protected CableRenderer(BlockEntityRendererProvider.Context context) {
        model = new CableModel(context.bakeLayer(PowahLayerDefinitions.CABLE));
    }

    @Override
    public CableRendererState createRenderState() {
        return new CableRendererState();
    }

    @Override
    public void submit(CableRendererState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.translate(0.0, -0.125, 0.0);
        poseStack.scale(1.0f, -1.0f, -1.0f);
        submitNodeCollector.submitModel(model, state, poseStack, Sheets.cutoutBlockSheet(), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, null, 0, null);
        poseStack.popPose();
    }
}
