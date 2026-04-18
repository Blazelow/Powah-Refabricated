package owmii.powah.client.render.tile;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class BlockEntityRendererRegistry {
    public static void register() {
        BlockEntityRenderers.register(Tiles.CABLE, CableRenderer::new);
        BlockEntityRenderers.register(Tiles.ENERGIZING_ORB, EnergizingOrbRenderer::new);
        BlockEntityRenderers.register(Tiles.ENERGIZING_ROD, EnergizingRodRenderer::new);
        BlockEntityRenderers.register(Tiles.FURNATOR, FurnatorRenderer::new);
        BlockEntityRenderers.register(Tiles.MAGMATOR, MagmatorRenderer::new);
        BlockEntityRenderers.register(Tiles.REACTOR, ReactorRenderer::new);
        BlockEntityRenderers.register(Tiles.REACTOR_PART, ReactorPartRenderer::new);
    }
}
