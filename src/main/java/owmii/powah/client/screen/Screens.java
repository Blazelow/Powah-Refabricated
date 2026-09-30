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
        MenuScreens.register(Containers.ENERGY_CELL.get(), EnergyCellScreen::new);
        MenuScreens.register(Containers.ENDER_CELL.get(), EnderCellScreen::new);
        MenuScreens.register(Containers.FURNATOR.get(), FurnatorScreen::new);
        MenuScreens.register(Containers.MAGMATOR.get(), MagmatorScreen::new);
        MenuScreens.register(Containers.PLAYER_TRANSMITTER.get(), PlayerTransmitterScreen::new);
        MenuScreens.register(Containers.ENERGY_HOPPER.get(), EnergyHopperScreen::new);
        MenuScreens.register(Containers.CABLE.get(), CableScreen::new);
        MenuScreens.register(Containers.REACTOR.get(), ReactorScreen::new);
        MenuScreens.register(Containers.SOLAR.get(), SolarScreen::new);
        MenuScreens.register(Containers.THERMO.get(), ThermoScreen::new);
        MenuScreens.register(Containers.DISCHARGER.get(), DischargerScreen::new);
    }
}
