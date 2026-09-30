package owmii.powah.util;

import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ContainerStorage;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import owmii.powah.ChargeableItemsEvent;
import owmii.powah.lib.logistics.inventory.Inventory;
import team.reborn.energy.api.EnergyStorage;

public final class ChargeUtil {
    private ChargeUtil() {
    }

    public static long chargeItemsInPlayerInv(Player player, long maxPerSlot, long maxTotal, TransactionContext tx) {
        return chargeItemsInPlayerInv(player, maxPerSlot, maxTotal, _ -> true, tx);
    }

    public static long chargeItemsInPlayerInv(Player player, long maxPerSlot, long maxTotal, Predicate<ItemVariant> allowStack,
            TransactionContext tx) {
        var playerStorage = PlayerInventoryStorage.of(player);
        Stream<ContainerItemContext> slots = playerStorage.getSlots().stream().map(slot -> ContainerItemContext.ofPlayerSlot(player, slot));

        var event = new ChargeableItemsEvent(player);
        ChargeableItemsEvent.EVENT.invoker().accept(event);
        for (var source : event.getSources()) {
            slots = Stream.concat(slots, source);
        }

        slots = slots.filter(context -> allowStack.test(context.getItemVariant()));

        return transferSlotList(EnergyStorage::insert, slots, maxPerSlot, maxTotal, tx);
    }

    public static long chargeItemsInContainer(Container container, long maxPerSlot, long maxTotal, TransactionContext tx) {
        var storage = ContainerStorage.of(container, null);
        Stream<ContainerItemContext> contexts = storage.getSlots().stream().map(ContainerItemContext::ofSingleSlot);
        var ret = transferSlotList(EnergyStorage::insert, contexts, maxPerSlot, maxTotal, tx);
        if (ret > 0) {
            container.setChanged();
        }
        return ret;
    }

    public static long chargeItemsInInventory(Inventory inv, int slotFrom, int slotTo, long maxPerSlot, long maxTotal, TransactionContext tx) {
        Stream<ContainerItemContext> contexts = IntStream.range(slotFrom, slotTo)
                .mapToObj(index -> ContainerItemContext.ofSingleSlot(inv.getSlot(index)));
        return transferSlotList(EnergyStorage::insert, contexts, maxPerSlot, maxTotal, tx);
    }

    public static long dischargeItemsInInventory(Inventory inv, long maxPerSlot, long maxTotal, TransactionContext tx) {
        Stream<ContainerItemContext> contexts = IntStream.range(0, inv.size())
                .mapToObj(index -> ContainerItemContext.ofSingleSlot(inv.getSlot(index)));
        return transferSlotList(EnergyStorage::extract, contexts, maxPerSlot, maxTotal, tx);
    }

    private static long transferSlotList(EnergyTransferOperation op, Stream<ContainerItemContext> contexts, long maxPerStack, long maxTotal,
            TransactionContext tx) {
        long charged = 0;
        var it = contexts.iterator();
        while (it.hasNext()) {
            var context = it.next();
            var storage = EnergyStorage.ITEM.find(context.getItemVariant().toStack(), context);
            if (storage != null) {
                charged += op.perform(storage, Math.min(maxPerStack, maxTotal - charged), tx);
            }
        }
        return charged;
    }

    public static boolean isChargeableItem(ItemStack stack) {
        var energy = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        return energy != null && energy.getCapacity() > 0;
    }

    public static long getStored(ItemStack stack) {
        var energy = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        return energy != null ? energy.getAmount() : 0L;
    }

    interface EnergyTransferOperation {
        long perform(EnergyStorage storage, long maxAmount, TransactionContext tx);
    }
}
