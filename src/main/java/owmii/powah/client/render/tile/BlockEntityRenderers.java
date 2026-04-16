package owmii.powah.client.render.tile;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererFactories;
import owmii.powah.block.Tiles;

public class BlockEntityRenderers {
    public static void register() {
        BlockEntityRendererFactories.register(Tiles.CABLE, CableRenderer::new);
        BlockEntityRendererFactories.register(Tiles.ENERGIZING_ORB, EnergizingOrbRenderer::new);
        BlockEntityRendererFactories.register(Tiles.ENERGIZING_ROD, EnergizingRodRenderer::new);
        BlockEntityRendererFactories.register(Tiles.FURNATOR, FurnatorRenderer::new);
        BlockEntityRendererFactories.register(Tiles.MAGMATOR, MagmatorRenderer::new);
        BlockEntityRendererFactories.register(Tiles.REACTOR, ReactorRenderer::new);
        BlockEntityRendererFactories.register(Tiles.REACTOR_PART, ReactorPartRenderer::new);
    }
}
