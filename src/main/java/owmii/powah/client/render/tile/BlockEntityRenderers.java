package owmii.powah.client.render.tile;

import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import owmii.powah.block.Tiles;

public class BlockEntityRenderers {
    public static void register() {
        BlockEntityRendererRegistry.register(Tiles.CABLE.get(), CableRenderer::new);
        BlockEntityRendererRegistry.register(Tiles.ENERGIZING_ORB.get(), EnergizingOrbRenderer::new);
        BlockEntityRendererRegistry.register(Tiles.ENERGIZING_ROD.get(), EnergizingRodRenderer::new);
        BlockEntityRendererRegistry.register(Tiles.FURNATOR.get(), FurnatorRenderer::new);
        BlockEntityRendererRegistry.register(Tiles.MAGMATOR.get(), MagmatorRenderer::new);
        BlockEntityRendererRegistry.register(Tiles.REACTOR.get(), ReactorRenderer::new);
        BlockEntityRendererRegistry.register(Tiles.REACTOR_PART.get(), ReactorPartRenderer::new);
    }
}
