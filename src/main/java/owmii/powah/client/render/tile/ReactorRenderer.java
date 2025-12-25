package owmii.powah.client.render.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.block.reactor.ReactorBlockEntity;
import owmii.powah.client.model.CubeModel;
import owmii.powah.client.model.PowahLayerDefinitions;
import owmii.powah.client.model.ReactorModel;

public class ReactorRenderer implements BlockEntityRenderer<ReactorBlockEntity, ReactorRendererState> {
    private final ReactorModel reactorModel;
    private final CubeModel reactorPartModel;

    protected ReactorRenderer(BlockEntityRendererProvider.Context context) {
        reactorModel = new ReactorModel(context.bakeLayer(PowahLayerDefinitions.REACTOR));
        reactorPartModel = new CubeModel(RenderTypes::entitySolid, context.bakeLayer(PowahLayerDefinitions.REACTOR_PART));
    }

    @Override
    public ReactorRendererState createRenderState() {
        return new ReactorRendererState();
    }

    @Override
    public void extractRenderState(ReactorBlockEntity blockEntity, ReactorRendererState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.update(blockEntity);
    }

    @Override
    public void submit(ReactorRendererState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(1.0f, -1.0f, -1.0f);
        if (!state.built) {
            var renderType = RenderTypes.entitySolid(ReactorPartRenderer.getTexture(state.tier));
            submitNodeCollector.submitModel(reactorPartModel, state, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, 0, null);
        } else {
            poseStack.translate(0.0D, -1.0D, 0.0D);
            submitNodeCollector.submitModel(reactorModel, state, poseStack, Sheets.solidBlockSheet(), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, null);
        }
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(ReactorBlockEntity blockEntity) {
        return new AABB(blockEntity.getBlockPos()).inflate(1.0D, 3.0D, 1.0D);
    }
}
