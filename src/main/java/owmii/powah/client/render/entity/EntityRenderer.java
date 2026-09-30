package owmii.powah.client.render.entity;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import owmii.powah.entity.Entities;

public class EntityRenderer {
    public static void register() {
        EntityRendererRegistry.register(Entities.CHARGED_SNOWBALL.get(), ThrownItemRenderer::new);
    }
}
