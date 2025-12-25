package owmii.powah.client;

import guideme.Guide;
import guideme.compiler.TagCompiler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.jspecify.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.block.energizing.EnergizingOrbBlock;
import owmii.powah.block.energizing.EnergizingRodBlock;
import owmii.powah.client.book.PowahTagCompiler;
import owmii.powah.client.handler.HudHandler;
import owmii.powah.client.handler.ReactorOverlayHandler;
import owmii.powah.client.model.PowahLayerDefinitions;
import owmii.powah.client.render.entity.EntityRenderer;
import owmii.powah.client.render.hud.BlockHudRenderer;
import owmii.powah.client.render.hud.EnergizingOrbHudRenderer;
import owmii.powah.client.render.hud.EnergizingRodHudRenderer;
import owmii.powah.client.render.hud.ItemHudRenderer;
import owmii.powah.client.render.tile.BlockEntityRenderers;
import owmii.powah.client.render.tile.ReactorItemRenderer;
import owmii.powah.client.screen.Screens;
import owmii.powah.item.PowahBookItem;
import owmii.powah.lib.client.util.RenderTypes;

@Mod(value = Powah.MOD_ID, dist = Dist.CLIENT)
public final class PowahClient {
    public PowahClient(IEventBus modEventBus) {
        modEventBus.addListener(PowahClient::clientSetup);

        modEventBus.addListener(PowahLayerDefinitions::register);
        HudHandler.register(this);
        modEventBus.addListener(EntityRenderer::register);
        modEventBus.addListener(Screens::register);
        modEventBus.addListener(BlockEntityRenderers::register);
        modEventBus.addListener(this::registerSpecialItemRenderers);
        modEventBus.addListener(this::registerRenderPipelines);

        NeoForge.EVENT_BUS.addListener((RenderLevelStageEvent.AfterLevel event) -> {
            ReactorOverlayHandler.onRenderLast(event.getPoseStack(), event.getLevelRenderState().cameraRenderState);
        });

        Guide.builder(PowahBookItem.GUIDE_ID)
                .defaultLanguage("en_us")
                .extension(TagCompiler.EXTENSION_POINT, new PowahTagCompiler())
                .build();
    }

    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(PowahClient::clientSetupSequential);
    }

    public static void clientSetupSequential() {
        ItemModelProperties.register();
    }

    private void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(RenderTypes.REACTOR_OVERLAY);
        event.registerPipeline(RenderTypes.BLENDED_NO_DEPTH);
    }

    private void registerSpecialItemRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(ReactorItemRenderer.ID, ReactorItemRenderer.Unbaked.MAP_CODEC);
    }

    private final EnergizingOrbHudRenderer energizingOrbHudRenderer = new EnergizingOrbHudRenderer();
    private final EnergizingRodHudRenderer energizingRodHudRenderer = new EnergizingRodHudRenderer();

    @Nullable
    public BlockHudRenderer getBlockHudRenderer(BlockState state) {
        if (state.getBlock() instanceof EnergizingOrbBlock) {
            return energizingOrbHudRenderer;
        } else if (state.getBlock() instanceof EnergizingRodBlock) {
            return energizingRodHudRenderer;
        }
        return null;
    }

    @Nullable
    public ItemHudRenderer getItemHudRenderer(ItemStack stack) {
        return null;
    }
}
