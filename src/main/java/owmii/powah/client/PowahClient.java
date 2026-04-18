package owmii.powah.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import owmii.powah.client.handler.HudHandler;
import owmii.powah.client.handler.ReactorOverlayHandler;
import owmii.powah.client.model.PowahLayerDefinitions;
import owmii.powah.client.render.entity.EntityRenderer;
import owmii.powah.client.render.tile.BlockEntityRendererRegistry;
import owmii.powah.client.screen.Screens;
import owmii.powah.lib.client.util.MC;
import owmii.powah.client.render.tile.ReactorItemRenderer;
import owmii.powah.block.Blcks;
import owmii.powah.item.ReactorItem;
import owmii.powah.network.Network;

@Environment(EnvType.CLIENT)
public final class PowahClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PowahLayerDefinitions.register();
        HudHandler.register();
        EntityRenderer.register();
        Screens.register();
        BlockEntityRendererRegistry.register();

        ClientTickEvents.END_CLIENT_TICK.register(mc -> MC.onTick());

        WorldRenderEvents.LAST.register(context ->
                ReactorOverlayHandler.onRenderLast(context.matrixStack(), context.camera()));

        ItemModelProperties.register();

        // ReactorItem custom BEWLR - register for all tier variants
        var reactorRenderer = new ReactorItemRenderer();
        Blcks.REACTOR.getAll().forEach(block -> {
            if (block.asItem() instanceof ReactorItem) {
                BuiltinItemRendererRegistry.INSTANCE.register(block.asItem(), reactorRenderer);
            }
        });

        Network.registerClient();
    }
}
