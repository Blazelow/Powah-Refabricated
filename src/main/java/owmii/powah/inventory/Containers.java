package owmii.powah.inventory;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import owmii.powah.lib.registry.DeferredRegister;
import owmii.powah.Powah;

public class Containers {
    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(Registries.MENU, Powah.MOD_ID);

    public static final Supplier<MenuType<EnergyCellContainer>> ENERGY_CELL = DR.register("energy_cell",
            () -> create(EnergyCellContainer::create));
    public static final Supplier<MenuType<EnderCellContainer>> ENDER_CELL = DR.register("ender_cell",
            () -> create(EnderCellContainer::create));
    public static final Supplier<MenuType<FurnatorContainer>> FURNATOR = DR.register("furnator",
            () -> create(FurnatorContainer::create));
    public static final Supplier<MenuType<MagmatorContainer>> MAGMATOR = DR.register("magmator",
            () -> create(MagmatorContainer::create));
    public static final Supplier<MenuType<PlayerTransmitterContainer>> PLAYER_TRANSMITTER = DR.register("player_transmitter",
            () -> create(PlayerTransmitterContainer::create));
    public static final Supplier<MenuType<EnergyHopperContainer>> ENERGY_HOPPER = DR.register("energy_hopper",
            () -> create(EnergyHopperContainer::create));
    public static final Supplier<MenuType<CableContainer>> CABLE = DR.register("cable", () -> create(CableContainer::create));
    public static final Supplier<MenuType<ReactorContainer>> REACTOR = DR.register("reactor",
            () -> create(ReactorContainer::create));
    public static final Supplier<MenuType<SolarContainer>> SOLAR = DR.register("solar", () -> create(SolarContainer::create));
    public static final Supplier<MenuType<ThermoContainer>> THERMO = DR.register("thermo", () -> create(ThermoContainer::create));
    public static final Supplier<MenuType<DischargerContainer>> DISCHARGER = DR.register("discharger",
            () -> create(DischargerContainer::create));

    private static <T extends AbstractContainerMenu> MenuType<T> create(MenuFactory<T> factory) {
        return new ExtendedScreenHandlerType<>((id, inventory, data) -> factory.create(id, inventory, data.toBuffer()), MenuData.CODEC);
    }

    @FunctionalInterface
    private interface MenuFactory<T extends AbstractContainerMenu> {
        T create(int id, Inventory inventory, FriendlyByteBuf buffer);
    }
}
