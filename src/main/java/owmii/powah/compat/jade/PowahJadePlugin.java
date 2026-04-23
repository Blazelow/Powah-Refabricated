package owmii.powah.compat.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import owmii.powah.Powah;
import owmii.powah.block.Blcks;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class PowahJadePlugin implements IWailaPlugin {

    public static final ResourceLocation ENERGY_UID = Powah.id("energy");

    @Override
    public void register(IWailaCommonRegistration registration) {
        // Register server-side data provider for every Powah block that has energy.
        // Jade registers by Block class, so we enumerate each block family explicitly.
        registerEnergyBlocks(registration);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registerEnergyBlocksClient(registration);
    }

    private static void registerEnergyBlocks(IWailaCommonRegistration registration) {
        Blcks.SOLAR_PANEL.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.FURNATOR.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.MAGMATOR.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.THERMO_GENERATOR.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_CELL.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENDER_CELL.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENDER_GATE.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGIZING_ROD.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.REACTOR.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, Blcks.CABLE.getClass());
        registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, Blcks.PLAYER_TRANSMITTER.getClass());
        registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, Blcks.ENERGY_HOPPER.getClass());
        registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, Blcks.ENERGY_DISCHARGER.getClass());
    }

    private static void registerEnergyBlocksClient(IWailaClientRegistration registration) {
        Blcks.SOLAR_PANEL.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.FURNATOR.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.MAGMATOR.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.THERMO_GENERATOR.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_CELL.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENDER_CELL.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENDER_GATE.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGIZING_ROD.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.REACTOR.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, Blcks.CABLE.getClass());
        registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, Blcks.PLAYER_TRANSMITTER.getClass());
        registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, Blcks.ENERGY_HOPPER.getClass());
        registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, Blcks.ENERGY_DISCHARGER.getClass());
    }
}
