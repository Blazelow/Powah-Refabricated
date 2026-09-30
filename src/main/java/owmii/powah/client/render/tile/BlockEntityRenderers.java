package owmii.powah.client.render.tile;

import owmii.powah.block.Tiles;

public class BlockEntityRenderers {
    public static void register() {
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(Tiles.CABLE.get(), CableRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(Tiles.ENERGIZING_ORB.get(), EnergizingOrbRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(Tiles.ENERGIZING_ROD.get(), EnergizingRodRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(Tiles.FURNATOR.get(), FurnatorRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(Tiles.MAGMATOR.get(), MagmatorRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(Tiles.REACTOR.get(), ReactorRenderer::new);
        net.minecraft.client.renderer.blockentity.BlockEntityRenderers.register(Tiles.REACTOR_PART.get(), ReactorPartRenderer::new);
    }
}
