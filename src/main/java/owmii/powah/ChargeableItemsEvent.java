package owmii.powah;

import java.util.List;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Fired when gathering items to charge in a player inventory.
 * Add stacks to the list to make them chargeable by Powah.
 *
 * Replaces the NeoForge Event subclass with a Fabric Event.
 */
public final class ChargeableItemsEvent {

    public static final Event<GatherItems> EVENT = EventFactory.createArrayBacked(GatherItems.class,
            listeners -> (player, items) -> {
                for (GatherItems listener : listeners) {
                    listener.gatherItems(player, items);
                }
            });

    @FunctionalInterface
    public interface GatherItems {
        void gatherItems(Player player, List<ItemStack> items);
    }

    private ChargeableItemsEvent() {
    }
}
