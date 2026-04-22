package owmii.powah.compat.jade;

import net.minecraft.resources.ResourceLocation;
import owmii.powah.Powah;
import owmii.powah.lib.block.AbstractEnergyStorage;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class PowahJadePlugin implements IWailaPlugin {

    public static final ResourceLocation ENERGY_UID = Powah.id("energy");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(PowahEnergyProvider.INSTANCE, AbstractEnergyStorage.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(PowahEnergyProvider.INSTANCE, AbstractEnergyStorage.class);
        registration.markAsClientFeature(ENERGY_UID);
    }
}
