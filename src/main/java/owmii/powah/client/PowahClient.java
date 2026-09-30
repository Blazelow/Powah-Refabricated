package owmii.powah.client;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.recipe.v1.sync.ClientRecipeSynchronizedEvent;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.energizing.EnergizingOrbBlock;
import owmii.powah.block.energizing.EnergizingRecipe;
import owmii.powah.block.energizing.EnergizingRodBlock;
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
import owmii.powah.lib.client.util.RenderTypes;
import owmii.powah.lib.datamap.DataMaps;
import owmii.powah.recipe.Recipes;

public final class PowahClient implements ClientModInitializer {
    public static final List<RecipeHolder<EnergizingRecipe>> ENERGIZING_RECIPES = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        RenderPipelines.register(RenderTypes.GUI_TEXTURED_NOBLEND);
        RenderPipelines.register(RenderTypes.REACTOR_OVERLAY);
        RenderPipelines.register(RenderTypes.BLENDED_NO_DEPTH);
        PowahLayerDefinitions.register();
        HudHandler.register(this);
        EntityRenderer.register();
        Screens.register();
        BlockEntityRenderers.register();
        DataMaps.initClient();
        SpecialModelRenderers.ID_MAPPER.put(ReactorItemRenderer.ID, ReactorItemRenderer.Unbaked.MAP_CODEC);

        LevelRenderEvents.COLLECT_SUBMITS.register(context -> ReactorOverlayHandler.onRenderLast(context.poseStack(), context.levelState().cameraRenderState,
                context.submitNodeCollector()));
        ClientRecipeSynchronizedEvent.EVENT.register((client, recipes) -> {
            ENERGIZING_RECIPES.clear();
            ENERGIZING_RECIPES.addAll(recipes.<net.minecraft.world.item.crafting.RecipeInput, EnergizingRecipe>getAllOfType(Recipes.ENERGIZING.get()));
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ENERGIZING_RECIPES.clear());
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
