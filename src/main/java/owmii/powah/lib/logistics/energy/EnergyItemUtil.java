package owmii.powah.lib.logistics.energy;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;

public final class EnergyItemUtil {
    private EnergyItemUtil() {
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
}
