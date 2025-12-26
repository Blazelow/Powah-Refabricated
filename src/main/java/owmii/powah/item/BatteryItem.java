package owmii.powah.item;

import com.google.common.primitives.Ints;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.api.energy.endernetwork.IEnderExtender;
import owmii.powah.block.Tier;
import owmii.powah.components.PowahComponents;
import owmii.powah.config.IEnergyConfig;
import owmii.powah.config.v2.types.EnergyConfig;
import owmii.powah.lib.item.EnergyItem;
import owmii.powah.util.ChargeUtil;

public class BatteryItem extends EnergyItem<Tier, EnergyConfig, BatteryItem> implements IEnderExtender {
    public BatteryItem(Item.Properties properties, Tier variant) {
        super(properties, variant);
    }

    @Override
    public IEnergyConfig<Tier> getConfig() {
        return Powah.config().devices.batteries;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (owner instanceof Player player && isCharging(itemStack)) {
            // Annoyingly we need to figure out where in the inventory we are, and same item/same components
            // is not enough, since we're about to modify.
            ItemAccess ourAccess = null;
            var playerInv = player.getInventory();
            for (int i = 0; i < playerInv.getContainerSize(); i++) {
                if (playerInv.getItem(i) == itemStack) {
                    ourAccess = ItemAccess.forPlayerSlot(player, i);
                    break;
                }
            }

            if (ourAccess == null) {
                return;
            }

            var storage = ourAccess.getCapability(Capabilities.Energy.ITEM);
            if (storage != null) {
                int maxExtract = Ints.saturatedCast(getConfig().getTransfer(getVariant()));
                try (var tx = Transaction.openRoot()) {
                    int charged = Ints.saturatedCast(ChargeUtil.chargeItemsInPlayerInv(player, maxExtract, storage.getAmountAsInt(),
                            s -> !(s.getItem() instanceof BatteryItem), tx));
                    storage.extract(charged, tx);
                    tx.commit();
                }
            }
        }
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            switchCharging(stack);
            return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
        }
        return super.use(world, player, hand);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        var energy = ChargeUtil.getStored(stack);
        return energy < getConfig().getCapacity(getVariant());
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        var energy = ChargeUtil.getStored(stack);
        return (int) Math.min(1 + 12 * energy / getConfig().getCapacity(getVariant()), 13);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isCharging(stack);
    }

    private void switchCharging(ItemStack stack) {
        setCharging(stack, !isCharging(stack));
    }

    private boolean isCharging(ItemStack stack) {
        return Objects.requireNonNullElse(stack.get(PowahComponents.CHARGING), false);
    }

    private void setCharging(ItemStack stack, boolean charging) {
        if (!charging) {
            stack.remove(PowahComponents.CHARGING);
        } else {
            stack.set(PowahComponents.CHARGING, true);
        }
    }

    @Override
    public long getExtendedCapacity(ItemStack stack) {
        return getConfig().getCapacity(getVariant());
    }

    @Override
    public long getExtendedEnergy(ItemStack stack) {
        return ChargeUtil.getStored(stack);
    }
}
