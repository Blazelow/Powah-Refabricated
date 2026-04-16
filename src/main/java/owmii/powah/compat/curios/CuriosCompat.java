// NOTE: Curios (NeoForge) has no direct Fabric equivalent.
// To support trinket slots on Fabric, integrate with the Trinkets mod instead.
// This file is kept as a stub. All Curios-specific code has been removed.

package owmii.powah.compat.curios;

import net.minecraft.world.item.ItemStack;
import owmii.powah.ChargeableItemsEvent;
import top.theillusivec4.curios.api.CuriosCapability;

public class CuriosCompat {
    public static void init() {
        NeoForge.EVENT_BUS.addListener(CuriosCompat::addCurioStacks);
    }

    // I don't really like this, hopefully it doesn't crash...
    public static void addCurioStacks(ChargeableItemsEvent event) {
        var curiosInventory = event.getPlayer().getCapability(CuriosCapability.INVENTORY);
        if (curiosInventory == null) {
            return;
        }

        curiosInventory.getCurios().forEach((s, stackHandler) -> {
            for (int i = 0; i < stackHandler.getSlots(); i++) {
                ItemStack stack = stackHandler.getStacks().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    event.getItems().add(stack);
                }
            }
        });
    }
}
