package owmii.powah.lib.client.util;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import owmii.powah.Powah;

public class RenderTypes {
    public static RenderPipeline REACTOR_OVERLAY = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withSampler("Sampler0")
            .withBlend(BlendFunction.LIGHTNING)
            .withDepthWrite(true)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withLocation(Powah.id("reactor_overlay"))
            .build();

    public static RenderPipeline BLENDED_NO_DEPTH = RenderPipeline.builder()
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withSampler("Sampler0")
            .withBlend(BlendFunction.LIGHTNING)
            .withDepthWrite(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .withLocation(Powah.id("blended"))
            .withLocation(Powah.id("blended_no_depth"))
            .build();

    public static RenderType entityBlendedNoDept(Identifier location) {
        return makeBlendNoDept(location);
    }

    public static RenderType makeBlendNoDept(Identifier location) {
        return RenderType.create("blend_bo_dept", RenderSetup.builder(BLENDED_NO_DEPTH)
                .withTexture("Sampler0", location)
                .bufferSize(256)
                .sortOnUpload()
                .affectsCrumbling()
                .createRenderSetup());
    }

    public static RenderType createReactorOverlay(Identifier location) {
        return RenderType.create("blend_bo_dept", RenderSetup.builder(REACTOR_OVERLAY)
                .withTexture("Sampler0", location)
                .bufferSize(256)
                .sortOnUpload()
                .createRenderSetup());
    }
}
