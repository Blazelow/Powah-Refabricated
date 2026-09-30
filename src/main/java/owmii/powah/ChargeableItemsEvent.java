package owmii.powah;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.world.entity.player.Player;

public class ChargeableItemsEvent {
    public static final Event<Consumer<ChargeableItemsEvent>> EVENT = EventFactory.createArrayBacked(Consumer.class, listeners -> event -> {
        for (var listener : listeners) {
            listener.accept(event);
        }
    });

    private final Player player;
    private final List<Stream<ContainerItemContext>> sources = new ArrayList<>();

    public ChargeableItemsEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Stream<ContainerItemContext>> getSources() {
        return sources;
    }
}
