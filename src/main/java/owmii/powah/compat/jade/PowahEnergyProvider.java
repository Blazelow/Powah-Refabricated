package owmii.powah.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import owmii.powah.lib.block.AbstractEnergyStorage;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public class PowahEnergyProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final PowahEnergyProvider INSTANCE = new PowahEnergyProvider();
    private static final String KEY_STORED = "powah_energy_stored";
    private static final String KEY_MAX    = "powah_energy_max";

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        BlockEntity be = accessor.getBlockEntity();
        if (be instanceof AbstractEnergyStorage<?, ?> energyBe) {
            var energy = energyBe.getEnergy();
            data.putLong(KEY_STORED, energy.getEnergyStored());
            data.putLong(KEY_MAX, energy.getMaxEnergyStored());
        }
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (!data.contains(KEY_STORED)) return;

        long stored = data.getLong(KEY_STORED);
        long max    = data.getLong(KEY_MAX);
        if (max <= 0) return;

        IElementHelper helper = IElementHelper.get();
        // Use the no-arg progress overload to avoid BoxStyle issues across Jade versions
        IElement bar = helper.progress(
                (float) stored / max,
                net.minecraft.network.chat.Component.literal(
                        owmii.powah.util.Util.addCommas(stored) + " / " + owmii.powah.util.Util.addCommas(max) + " FE"),
                helper.progressStyle()
                        .color(0xFF3355FF, 0xFF223399)
                        .textColor(0xFFFFFFFF),
                null,
                true);
        tooltip.add(bar);
    }

    @Override
    public ResourceLocation getUid() {
        return PowahJadePlugin.ENERGY_UID;
    }
}
