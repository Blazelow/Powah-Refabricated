package owmii.powah.client.screen;

import net.minecraft.client.gui.screens.MenuScreens;
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
        MenuScreens.register(Containers.ENERGY_CELL, EnergyCellScreen::new);
        MenuScreens.register(Containers.ENDER_CELL, EnderCellScreen::new);
        MenuScreens.register(Containers.FURNATOR, FurnatorScreen::new);
        MenuScreens.register(Containers.MAGMATOR, MagmatorScreen::new);
        MenuScreens.register(Containers.PLAYER_TRANSMITTER, PlayerTransmitterScreen::new);
        MenuScreens.register(Containers.ENERGY_HOPPER, EnergyHopperScreen::new);
        MenuScreens.register(Containers.CABLE, CableScreen::new);
        MenuScreens.register(Containers.REACTOR, ReactorScreen::new);
        MenuScreens.register(Containers.SOLAR, SolarScreen::new);
        MenuScreens.register(Containers.THERMO, ThermoScreen::new);
        MenuScreens.register(Containers.DISCHARGER, DischargerScreen::new);
    }
}
