package owmii.powah.inventory;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;
import owmii.powah.Powah;

public class Containers {
    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(Registries.MENU, Powah.MOD_ID);

    public static final Supplier<MenuType<EnergyCellMenu>> ENERGY_CELL = DR.register("energy_cell",
            () -> IMenuTypeExtension.create(EnergyCellMenu::create));
    public static final Supplier<MenuType<EnderCellMenu>> ENDER_CELL = DR.register("ender_cell",
            () -> IMenuTypeExtension.create(EnderCellMenu::create));
    public static final Supplier<MenuType<FurnatorMenu>> FURNATOR = DR.register("furnator",
            () -> IMenuTypeExtension.create(FurnatorMenu::create));
    public static final Supplier<MenuType<MagmatorMenu>> MAGMATOR = DR.register("magmator",
            () -> IMenuTypeExtension.create(MagmatorMenu::create));
    public static final Supplier<MenuType<PlayerTransmitterMenu>> PLAYER_TRANSMITTER = DR.register("player_transmitter",
            () -> IMenuTypeExtension.create(PlayerTransmitterMenu::create));
    public static final Supplier<MenuType<EnergyHopperMenu>> ENERGY_HOPPER = DR.register("energy_hopper",
            () -> IMenuTypeExtension.create(EnergyHopperMenu::create));
    public static final Supplier<MenuType<CableMenu>> CABLE = DR.register("cable", () -> IMenuTypeExtension.create(CableMenu::create));
    public static final Supplier<MenuType<ReactorMenu>> REACTOR = DR.register("reactor",
            () -> IMenuTypeExtension.create(ReactorMenu::create));
    public static final Supplier<MenuType<SolarMenu>> SOLAR = DR.register("solar", () -> IMenuTypeExtension.create(SolarMenu::create));
    public static final Supplier<MenuType<ThermoMenu>> THERMO = DR.register("thermo", () -> IMenuTypeExtension.create(ThermoMenu::create));
    public static final Supplier<MenuType<DischargerMenu>> DISCHARGER = DR.register("discharger",
            () -> IMenuTypeExtension.create(DischargerMenu::create));
}
