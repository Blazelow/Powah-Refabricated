package owmii.powah.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import owmii.powah.client.handler.HudHandler;
import owmii.powah.client.handler.ReactorOverlayHandler;
import owmii.powah.client.model.PowahLayerDefinitions;
import owmii.powah.client.render.entity.EntityRenderer;
import owmii.powah.client.render.tile.BlockEntityRenderers;
import owmii.powah.client.render.tile.ReactorItemRenderer;
import owmii.powah.client.screen.Screens;
import owmii.powah.block.Blcks;
import owmii.powah.lib.datamap.DataMaps;

public final class PowahClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PowahLayerDefinitions.register();
        HudHandler.register();
        EntityRenderer.register();
        Screens.register();
        BlockEntityRenderers.register();
        ItemModelProperties.register();
        DataMaps.initClient();

        WorldRenderEvents.LAST.register(context -> ReactorOverlayHandler.onRenderLast(context.matrixStack(), context.camera()));

        var reactorRenderer = new ReactorItemRenderer();
        for (var reactor : Blcks.REACTOR.getAll()) {
            BuiltinItemRendererRegistry.INSTANCE.register(reactor.asItem(), reactorRenderer::renderByItem);
        }
    }
}
