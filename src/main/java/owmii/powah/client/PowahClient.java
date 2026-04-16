package owmii.powah.client;

import guideme.Guide;
import guideme.compiler.TagCompiler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import owmii.powah.client.book.PowahTagCompiler;
import owmii.powah.client.handler.HudHandler;
import owmii.powah.client.handler.ReactorOverlayHandler;
import owmii.powah.client.model.PowahLayerDefinitions;
import owmii.powah.client.render.entity.EntityRenderer;
import owmii.powah.client.render.tile.BlockEntityRenderers;
import owmii.powah.client.screen.Screens;
import owmii.powah.item.PowahBookItem;
import owmii.powah.lib.client.util.MC;
import owmii.powah.network.Network;

@Environment(EnvType.CLIENT)
public final class PowahClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PowahLayerDefinitions.register();
        HudHandler.register();
        EntityRenderer.register();
        Screens.register();
        BlockEntityRenderers.register();

        // Client tick counter (was @SubscribeEvent ClientTickEvent in NeoForge)
        ClientTickEvents.END_CLIENT_TICK.register(mc -> MC.onTick());

        // Reactor overlay (was RenderLevelStageEvent.AFTER_LEVEL in NeoForge)
        WorldRenderEvents.LAST.register(context ->
                ReactorOverlayHandler.onRenderLast(context.matrixStack(), context.camera()));

        // Item model property overrides
        ItemModelProperties.register();

        // GuideME book
        Guide.builder(PowahBookItem.GUIDE_ID)
                .defaultLanguage("en_us")
                .extension(TagCompiler.EXTENSION_POINT, new PowahTagCompiler())
                .build();

        Network.registerClient();
    }
}
