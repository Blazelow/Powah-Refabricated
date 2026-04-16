package owmii.powah.client.screen;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import owmii.powah.client.screen.container.CableScreen;
import owmii.powah.client.screen.container.DischargerScreen;
import owmii.powah.client.screen.container.EnderCellScreen;
import owmii.powah.client.screen.container.EnergyCellScreen;
import owmii.powah.client.screen.container.EnergyHopperScreen;
import owmii.powah.client.screen.container.FurnatorScreen;
import owmii.powah.client.screen.container.MagmatorScreen;
import owmii.powah.client.screen.container.PlayerTransmitterScreen;
import owmii.powah.client.screen.container.ReactorScreen;
import owmii.powah.client.screen.container.SolarScreen;
import owmii.powah.client.screen.container.ThermoScreen;
import owmii.powah.inventory.Containers;

public class Screens {
    public static void register() {
        ScreenRegistry.register(Containers.ENERGY_CELL, EnergyCellScreen::new);
        ScreenRegistry.register(Containers.ENDER_CELL, EnderCellScreen::new);
        ScreenRegistry.register(Containers.FURNATOR, FurnatorScreen::new);
        ScreenRegistry.register(Containers.MAGMATOR, MagmatorScreen::new);
        ScreenRegistry.register(Containers.PLAYER_TRANSMITTER, PlayerTransmitterScreen::new);
        ScreenRegistry.register(Containers.ENERGY_HOPPER, EnergyHopperScreen::new);
        ScreenRegistry.register(Containers.CABLE, CableScreen::new);
        ScreenRegistry.register(Containers.REACTOR, ReactorScreen::new);
        ScreenRegistry.register(Containers.SOLAR, SolarScreen::new);
        ScreenRegistry.register(Containers.THERMO, ThermoScreen::new);
        ScreenRegistry.register(Containers.DISCHARGER, DischargerScreen::new);
    }
}
