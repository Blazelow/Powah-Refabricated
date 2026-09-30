package owmii.powah;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ChargeableItemsEvent {
    public static final Event<Consumer<ChargeableItemsEvent>> EVENT = EventFactory.createArrayBacked(Consumer.class, listeners -> event -> {
        for (var listener : listeners) {
            listener.accept(event);
        }
    });

    private final Player player;
    private final List<ItemStack> items = new ArrayList<>();

    public ChargeableItemsEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
