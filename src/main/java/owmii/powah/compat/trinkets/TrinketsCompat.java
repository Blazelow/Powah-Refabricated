package owmii.powah.compat.trinkets;

import dev.emi.trinkets.api.TrinketsApi;
import owmii.powah.ChargeableItemsEvent;

public class TrinketsCompat {
    public static void init() {
        ChargeableItemsEvent.EVENT.register(TrinketsCompat::addTrinketStacks);
    }

    private static void addTrinketStacks(ChargeableItemsEvent event) {
        TrinketsApi.getTrinketComponent(event.getPlayer()).ifPresent(component -> component.forEach((slot, stack) -> {
            if (!stack.isEmpty()) {
                event.getItems().add(stack);
            }
        }));
    }
}
