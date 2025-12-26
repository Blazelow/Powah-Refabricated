package owmii.powah;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.transfer.access.ItemAccess;

/**
 * Fired when gathering items to charge in a player.
 * You can add stacks to the list to make them chargeable by Powah.
 */
public class ChargeableItemsEvent extends Event {
    private final Player player;
    private final List<Stream<ItemAccess>> sources = new ArrayList<>();

    public ChargeableItemsEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Stream<ItemAccess>> getSources() {
        return sources;
    }
}
