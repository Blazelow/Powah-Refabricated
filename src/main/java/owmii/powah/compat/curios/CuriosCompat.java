package owmii.powah.compat.curios;

import net.neoforged.neoforge.common.NeoForge;
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

        curiosInventory.getCurios().forEach((_, stackHandler) -> {
            for (int i = 0; i < stackHandler.getStacks().getSlots(); i++) {
                // TODO 26.1: Handle after curios update
            }
        });
    }

}
