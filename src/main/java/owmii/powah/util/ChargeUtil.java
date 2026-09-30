package owmii.powah.util;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import owmii.powah.ChargeableItemsEvent;
import owmii.powah.lib.logistics.inventory.Inventory;
import owmii.powah.lib.logistics.inventory.StackSlot;
import team.reborn.energy.api.EnergyStorage;

public final class ChargeUtil {
    private ChargeUtil() {
    }

    public static long chargeItemsInPlayerInv(Player player, long maxPerSlot, long maxTotal) {
        return chargeItemsInPlayerInv(player, maxPerSlot, maxTotal, s -> true);
    }

    public static boolean canDischarge(ItemStack stack) {
        var storage = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
        return storage != null && storage.supportsExtraction() && storage.getAmount() > 0;
    }

    public static long chargeItemsInPlayerInv(Player player, long maxPerSlot, long maxTotal, Predicate<ItemStack> allowStack) {
        var stacks = new ArrayList<>(owmii.powah.util.Player.invStacks(player).stream().toList());
        var event = new ChargeableItemsEvent(player);
        ChargeableItemsEvent.EVENT.invoker().accept(event);
        stacks.addAll(event.getItems());
        stacks.removeIf(allowStack.negate());
        return transferSlotList(EnergyStorage::insert, stacks, maxPerSlot, maxTotal);
    }

    public static long chargeItemsInContainer(Container container, long maxPerSlot, long maxTotal) {
        var ret = transferSlotList(EnergyStorage::insert,
                IntStream.range(0, container.getContainerSize()).mapToObj(container::getItem).toList(), maxPerSlot, maxTotal);
        container.setChanged();
        return ret;
    }

    public static long chargeItemsInInventory(Inventory inv, int slotFrom, int slotTo, long maxPerSlot, long maxTotal) {
        return transferSlotList(EnergyStorage::insert, IntStream.range(slotFrom, slotTo).mapToObj(inv::getStackInSlot).toList(), maxPerSlot,
                maxTotal);
    }

    public static long dischargeItemsInInventory(Inventory inv, long maxPerSlot, long maxTotal) {
        return transferSlotList(EnergyStorage::extract, IntStream.range(0, inv.getSlots()).mapToObj(inv::getStackInSlot).toList(), maxPerSlot,
                maxTotal);
    }

    private static long transferSlotList(EnergyTransferOperation op, Iterable<ItemStack> stacks, long maxPerStack, long maxTotal) {
        long charged = 0;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty())
                continue;
            long limit = Math.min(maxPerStack, maxTotal - charged);
            if (limit <= 0)
                break;
            var storage = EnergyStorage.ITEM.find(stack, ContainerItemContext.ofSingleSlot(new StackSlot(stack)));
            if (storage != null) {
                try (Transaction tx = Transaction.openOuter()) {
                    charged += op.perform(storage, limit, tx);
                    tx.commit();
                }
            }
        }
        return charged;
    }

    interface EnergyTransferOperation {
        long perform(EnergyStorage storage, long maxAmount, TransactionContext transaction);
    }
}
