package owmii.powah.inventory;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import owmii.powah.lib.registry.DeferredRegister;
import owmii.powah.Powah;

public class Containers {
    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(Registries.MENU, Powah.MOD_ID);

    public static final Supplier<MenuType<EnergyCellMenu>> ENERGY_CELL = DR.register("energy_cell",
            () -> create(EnergyCellMenu::create));
    public static final Supplier<MenuType<EnderCellMenu>> ENDER_CELL = DR.register("ender_cell",
            () -> create(EnderCellMenu::create));
    public static final Supplier<MenuType<FurnatorMenu>> FURNATOR = DR.register("furnator",
            () -> create(FurnatorMenu::create));
    public static final Supplier<MenuType<MagmatorMenu>> MAGMATOR = DR.register("magmator",
            () -> create(MagmatorMenu::create));
    public static final Supplier<MenuType<PlayerTransmitterMenu>> PLAYER_TRANSMITTER = DR.register("player_transmitter",
            () -> create(PlayerTransmitterMenu::create));
    public static final Supplier<MenuType<EnergyHopperMenu>> ENERGY_HOPPER = DR.register("energy_hopper",
            () -> create(EnergyHopperMenu::create));
    public static final Supplier<MenuType<CableMenu>> CABLE = DR.register("cable", () -> create(CableMenu::create));
    public static final Supplier<MenuType<ReactorMenu>> REACTOR = DR.register("reactor",
            () -> create(ReactorMenu::create));
    public static final Supplier<MenuType<SolarMenu>> SOLAR = DR.register("solar", () -> create(SolarMenu::create));
    public static final Supplier<MenuType<ThermoMenu>> THERMO = DR.register("thermo", () -> create(ThermoMenu::create));
    public static final Supplier<MenuType<DischargerMenu>> DISCHARGER = DR.register("discharger",
            () -> create(DischargerMenu::create));

    private static <T extends AbstractContainerMenu> MenuType<T> create(MenuFactory<T> factory) {
        return new ExtendedMenuType<>((id, inventory, data) -> factory.create(id, inventory, data.toBuffer()), MenuData.CODEC);
    }

    @FunctionalInterface
    private interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int id, Inventory inventory, FriendlyByteBuf buffer);
    }
}
