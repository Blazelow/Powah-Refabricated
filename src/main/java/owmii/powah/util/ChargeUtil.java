package owmii.powah.util;

import com.google.common.primitives.Ints;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import owmii.powah.ChargeableItemsEvent;
import owmii.powah.lib.logistics.inventory.Inventory;

/**
 * Utilities for charging and discharging items.
 */
public final class ChargeUtil {
    private ChargeUtil() {
    }

    // a bit ugly, but I couldn't find a better way
    public static long chargeItemsInPlayerInv(Player player, long maxPerSlot, long maxTotal, TransactionContext tx) {
        return chargeItemsInPlayerInv(player, maxPerSlot, maxTotal, s -> true, tx);
    }

    public static long chargeItemsInPlayerInv(Player player, long maxPerSlot, long maxTotal, Predicate<ItemResource> allowStack,
            TransactionContext tx) {
        var invWrapper = PlayerInventoryWrapper.of(player);
        Stream<ItemAccess> slots = IntStream.range(0, invWrapper.size()).mapToObj(index -> ItemAccess.forHandlerIndex(invWrapper, index));

        var event = new ChargeableItemsEvent(player);
        NeoForge.EVENT_BUS.post(event);
        for (var source : event.getSources()) {
            slots = Stream.concat(slots, source);
        }

        slots = slots.filter(access -> allowStack.test(access.getResource()));

        return transferSlotList(EnergyHandler::insert, slots, maxPerSlot, maxTotal, tx);
    }

    public static long chargeItemsInContainer(Container container, long maxPerSlot, long maxTotal, TransactionContext tx) {
        var containerWrapper = VanillaContainerWrapper.of(container);
        Stream<ItemAccess> itemAccesses = IntStream.range(0, containerWrapper.size())
                .mapToObj(index -> ItemAccess.forHandlerIndex(containerWrapper, index));
        var ret = transferSlotList(EnergyHandler::insert, itemAccesses, maxPerSlot, maxTotal, tx);
        if (ret > 0) {
            container.setChanged();
        }
        return ret;
    }

    public static long chargeItemsInInventory(Inventory inv, int slotFrom, int slotTo, long maxPerSlot, long maxTotal, TransactionContext tx) {
        // maybe call setChanged?
        Stream<ItemAccess> itemAccesses = IntStream.range(slotFrom, slotTo).mapToObj(index -> ItemAccess.forHandlerIndex(inv, index));
        return transferSlotList(EnergyHandler::insert, itemAccesses, maxPerSlot, maxTotal, tx);
    }

    public static long dischargeItemsInInventory(Inventory inv, long maxPerSlot, long maxTotal, TransactionContext tx) {
        // maybe call setChanged?
        Stream<ItemAccess> itemAccesses = IntStream.range(0, inv.size()).mapToObj(index -> ItemAccess.forHandlerIndex(inv, index));
        return transferSlotList(EnergyHandler::extract, itemAccesses, maxPerSlot, maxTotal, tx);
    }

    private static long transferSlotList(EnergyTransferOperation op, Stream<ItemAccess> itemAccesses, long maxPerStack, long maxTotal,
            TransactionContext tx) {
        long charged = 0;
        var it = itemAccesses.iterator();
        while (it.hasNext()) {
            var access = it.next();
            var cap = access.getCapability(Capabilities.Energy.ITEM);
            if (cap != null) {
                charged += op.perform(cap, Ints.saturatedCast(Math.min(maxPerStack, maxTotal - charged)), tx);
            }
        }
        return charged;
    }

    public static boolean isChargeableItem(ItemStack stack) {
        var energy = ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM);
        if (energy != null) {
            return energy.getCapacityAsLong() > 0;
        }
        return false;
    }

    public static long getStored(ItemStack stack) {
        var energy = ItemAccess.forStack(stack).getCapability(Capabilities.Energy.ITEM);
        if (energy != null) {
            return energy.getAmountAsLong();
        }
        return 0L;
    }

    interface EnergyTransferOperation {
        int perform(EnergyHandler storage, int maxAmount, TransactionContext tx);
    }
}
