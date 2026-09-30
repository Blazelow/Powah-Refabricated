package owmii.powah.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import owmii.powah.Powah;
import owmii.powah.components.PowahComponents;
import owmii.powah.item.Itms;

public class ItemModelProperties {
    public static void register() {
        ItemProperties.register(Itms.BINDING_CARD.get(), Powah.id("bound"), ItemModelProperties::renderBindingCard);
        ItemProperties.register(Itms.BINDING_CARD_DIM.get(), Powah.id("bound"), ItemModelProperties::renderBindingCard);
    }

    static float renderBindingCard(ItemStack stack, ClientLevel level, LivingEntity livingEntity, int seed) {
        float f = 0.0F;
        if (stack.has(PowahComponents.BOUND_PLAYER)) {
            f = 1.0F;
        }
        return f;
    }
}
