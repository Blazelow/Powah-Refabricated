package owmii.powah.util;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import owmii.powah.ChargeableItemsEvent;
import owmii.powah.lib.logistics.inventory.Inventory;
import team.reborn.energy.api.EnergyStorage;

/**
 * Utilities for charging and discharging items.
 * Replaces NeoForge IEnergyStorage / Capabilities.EnergyStorage.ITEM usage
 * with Team Reborn Energy via FAPI ContainerItemContext.
 */
public final class ChargeUtil {
    private ChargeUtil() {
    }

    public static boolean canDischarge(ItemStack stack) {
        var ctx = ContainerItemContext.withConstant(stack);
        var storage = EnergyStorage.ITEM.find(stack, ctx);
        return storage != null && storage.supportsExtraction() && storage.getAmount() > 0;
    }

    public static long chargeItemsInPlayerInv(Player player, long maxPerSlot, long maxTotal) {
        return chargeItemsInPlayerInv(player, maxPerSlot, maxTotal, s -> true);
    }

    public static long chargeItemsInPlayerInv(Player player, long maxPerSlot, long maxTotal, Predicate<ItemStack> allowStack) {
        var stacks = new ArrayList<>(owmii.powah.util.Player.invStacks(player).stream().toList());
        // Fire the Fabric equivalent of ChargeableItemsEvent
        ChargeableItemsEvent.EVENT.invoker().gatherItems(player, stacks);
        stacks.removeIf(allowStack.negate());
        return chargeStackList(stacks, maxPerSlot, maxTotal, true);
    }

    public static long chargeItemsInContainer(Container container, long maxPerSlot, long maxTotal) {
        var stacks = IntStream.range(0, container.getContainerSize()).mapToObj(container::getItem).toList();
        long ret = chargeStackList(stacks, maxPerSlot, maxTotal, true);
        container.setChanged();
        return ret;
    }

    public static long chargeItemsInInventory(Inventory inv, int slotFrom, int slotTo, long maxPerSlot, long maxTotal) {
        return chargeStackList(
                IntStream.range(slotFrom, slotTo).mapToObj(inv::getStackInSlot).toList(),
                maxPerSlot, maxTotal, true);
    }

    public static long dischargeItemsInInventory(Inventory inv, long maxPerSlot, long maxTotal) {
        return chargeStackList(
                IntStream.range(0, inv.getSlots()).mapToObj(inv::getStackInSlot).toList(),
                maxPerSlot, maxTotal, false);
    }

    /** @param insert true = charge (insert), false = discharge (extract) */
    private static long chargeStackList(Iterable<ItemStack> stacks, long maxPerStack, long maxTotal, boolean insert) {
        long total = 0;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            var ctx = ContainerItemContext.withConstant(stack);
            var storage = EnergyStorage.ITEM.find(stack, ctx);
            if (storage == null) continue;
            long limit = Math.min(maxPerStack, maxTotal - total);
            try (Transaction tx = Transaction.openOuter()) {
                long moved = insert ? storage.insert(limit, tx) : storage.extract(limit, tx);
                tx.commit();
                total += moved;
            }
        }
        return total;
    }
}
