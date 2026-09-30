package owmii.powah.compat.trinkets;

import eu.pb4.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import owmii.powah.ChargeableItemsEvent;

public class TrinketsCompat {
    public static void init() {
        ChargeableItemsEvent.EVENT.register(TrinketsCompat::addTrinketStacks);
    }

    private static void addTrinketStacks(ChargeableItemsEvent event) {
        var attachment = TrinketsApi.getAttachment(event.getPlayer());
        for (var inventory : attachment.getInventories().values()) {
            event.getSources().add(ContainerStorage.of(inventory, null).getSlots().stream().map(ContainerItemContext::ofSingleSlot));
        }
    }
}
