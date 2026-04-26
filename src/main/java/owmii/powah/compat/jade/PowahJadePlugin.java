package owmii.powah.compat.jade;

import net.minecraft.resources.ResourceLocation;
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
        // VarReg blocks — iterate all tiers
        Blcks.SOLAR_PANEL.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.FURNATOR.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.MAGMATOR.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.THERMO_GENERATOR.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_CELL.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENDER_CELL.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENDER_GATE.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGIZING_ROD.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.REACTOR.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_CABLE.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.PLAYER_TRANSMITTER.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_HOPPER.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_DISCHARGER.getAll().forEach(b -> registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, b.getClass()));
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        Blcks.SOLAR_PANEL.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.FURNATOR.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.MAGMATOR.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.THERMO_GENERATOR.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_CELL.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENDER_CELL.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENDER_GATE.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGIZING_ROD.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.REACTOR.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_CABLE.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.PLAYER_TRANSMITTER.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_HOPPER.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
        Blcks.ENERGY_DISCHARGER.getAll().forEach(b -> registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, b.getClass()));
    }
}
