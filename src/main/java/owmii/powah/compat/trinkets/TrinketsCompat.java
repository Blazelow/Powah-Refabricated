package owmii.powah.compat.trinkets;

import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.world.item.ItemStack;
import owmii.powah.ChargeableItemsEvent;

/**
 * Trinkets compat for Powah.
 * Gathers items from trinket slots so Powah can charge batteries worn as accessories.
 * Replaces NeoForge Curios compat.
 */
public class TrinketsCompat {

    public static void init() {
        ChargeableItemsEvent.EVENT.register((player, items) -> {
            TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
                component.forEach((slotReference, stack) -> {
                    if (!stack.isEmpty()) {
                        items.add(stack);
                    }
                    return true; // continue iterating
                });
            });
        });
    }
}
