package owmii.powah.inventory;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import owmii.powah.Powah;

public class Containers {

    public static final MenuType<EnergyCellContainer> ENERGY_CELL = register("energy_cell",
            (syncId, inv, buf) -> EnergyCellContainer.create(syncId, inv, buf));
    public static final MenuType<EnderCellContainer> ENDER_CELL = register("ender_cell",
            (syncId, inv, buf) -> EnderCellContainer.create(syncId, inv, buf));
    public static final MenuType<FurnatorContainer> FURNATOR = register("furnator",
            (syncId, inv, buf) -> FurnatorContainer.create(syncId, inv, buf));
    public static final MenuType<MagmatorContainer> MAGMATOR = register("magmator",
            (syncId, inv, buf) -> MagmatorContainer.create(syncId, inv, buf));
    public static final MenuType<PlayerTransmitterContainer> PLAYER_TRANSMITTER = register("player_transmitter",
            (syncId, inv, buf) -> PlayerTransmitterContainer.create(syncId, inv, buf));
    public static final MenuType<EnergyHopperContainer> ENERGY_HOPPER = register("energy_hopper",
            (syncId, inv, buf) -> EnergyHopperContainer.create(syncId, inv, buf));
    public static final MenuType<CableContainer> CABLE = register("cable",
            (syncId, inv, buf) -> CableContainer.create(syncId, inv, buf));
    public static final MenuType<ReactorContainer> REACTOR = register("reactor",
            (syncId, inv, buf) -> ReactorContainer.create(syncId, inv, buf));
    public static final MenuType<SolarContainer> SOLAR = register("solar",
            (syncId, inv, buf) -> SolarContainer.create(syncId, inv, buf));
    public static final MenuType<ThermoContainer> THERMO = register("thermo",
            (syncId, inv, buf) -> ThermoContainer.create(syncId, inv, buf));
    public static final MenuType<DischargerContainer> DISCHARGER = register("discharger",
            (syncId, inv, buf) -> DischargerContainer.create(syncId, inv, buf));

    @SuppressWarnings("deprecation")
    private static <T extends net.minecraft.world.inventory.AbstractContainerMenu> MenuType<T> register(
            String name, ScreenHandlerRegistry.ExtendedClientHandlerFactory<T> factory) {
        return ScreenHandlerRegistry.registerExtended(
                ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, name), factory);
    }

    public static void init() {
        // Called to trigger class loading and static registration
    }
}
