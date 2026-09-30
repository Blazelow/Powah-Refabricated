package owmii.powah.lib.client.util;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Optional;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import owmii.powah.Powah;

public class RenderTypes {
    public static final RenderPipeline GUI_TEXTURED_NOBLEND = copy(RenderPipelines.GUI_TEXTURED)
            .withColorTargetState(ColorTargetState.DEFAULT)
            .withLocation(Powah.id("gui_textured_noblend"))
            .build();

    public static RenderPipeline REACTOR_OVERLAY = copy(RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE)
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withLocation(Powah.id("reactor_overlay"))
            .build();

    public static RenderPipeline BLENDED_NO_DEPTH = copy(RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE)
            .withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
            .withCull(false)
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withLocation(Powah.id("blended_no_depth"))
            .build();

    private static RenderPipeline.Builder copy(RenderPipeline base) {
        var builder = RenderPipeline.builder()
                .withVertexShader(base.getVertexShader())
                .withFragmentShader(base.getFragmentShader())
                .withPrimitiveTopology(base.getPrimitiveTopology())
                .withCull(base.isCull())
                .withPolygonMode(base.getPolygonMode())
                .withColorTargetState(base.getColorTargetState())
                .withDepthStencilState(Optional.ofNullable(base.getDepthStencilState()));
        VertexFormat[] bindings = base.getVertexFormatBindings();
        for (int i = 0; i < bindings.length; i++) {
            builder.withVertexBinding(i, bindings[i]);
        }
        for (var layout : base.getBindGroupLayouts()) {
            builder.withBindGroupLayout(layout);
        }
        base.getShaderDefines().values().forEach((name, value) -> {
            try {
                builder.withShaderDefine(name, Integer.parseInt(value));
            } catch (NumberFormatException e) {
                builder.withShaderDefine(name, Float.parseFloat(value));
            }
        });
        base.getShaderDefines().flags().forEach(builder::withShaderDefine);
        return builder;
    }

    public static RenderType entityBlendedNoDepthWrite(Identifier location) {
        return RenderType.create("powah_blended_no_depth", RenderSetup.builder(BLENDED_NO_DEPTH)
                .withTexture("Sampler0", location)
                .sortOnUpload()
                .affectsCrumbling()
                .createRenderSetup());
    }

    public static RenderType createReactorOverlay(Identifier location) {
        return RenderType.create("powah_reactor_overlay", RenderSetup.builder(REACTOR_OVERLAY)
                .withTexture("Sampler0", location)
                .sortOnUpload()
                .createRenderSetup());
    }
}
