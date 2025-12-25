package owmii.powah.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import owmii.powah.components.PowahComponents;

public class ItemModelProperties {
    public static void register() {
        // TODO 26.1 ItemProperties.register(Itms.BINDING_CARD.get(), Powah.id("bound"), ItemModelProperties::renderBindingCard);
        // TODO 26.1 ItemProperties.register(Itms.BINDING_CARD_DIM.get(), Powah.id("bound"), ItemModelProperties::renderBindingCard);
    }

    static float renderBindingCard(ItemStack stack, ClientLevel level, LivingEntity livingEntity, int seed) {
        float f = 0.0F;
        if (stack.has(PowahComponents.BOUND_PLAYER)) {
            f = 1.0F;
        }
        return f;
    }
}
