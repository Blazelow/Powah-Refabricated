package owmii.powah.compat.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import owmii.powah.Powah;
import owmii.powah.api.PassiveHeatSourceConfig;
import owmii.powah.data.PowahDataLoader;

public record PassiveHeatSource(ResourceLocation id, @Nullable Block block, @Nullable Fluid fluid, int heat) {
    public static List<PassiveHeatSource> getAll() {
        var result = new ArrayList<PassiveHeatSource>();

        for (var block : BuiltInRegistries.BLOCK) {
            var id = BuiltInRegistries.BLOCK.getKey(block);
            var config = PowahDataLoader.getBlockHeatSource(block);
            if (config == null) continue;
            int heat = config.temperature();
            var recipeId = Powah.id("passive_heat_source/block/" + id.getNamespace() + "/" + id.getPath());
            result.add(new PassiveHeatSource(recipeId, block, null, heat));
        }

        for (var fluid : BuiltInRegistries.FLUID) {
            var id = BuiltInRegistries.FLUID.getKey(fluid);
            var config = PowahDataLoader.getFluidHeatSource(fluid);
            if (config == null) continue;
            int heat = config.temperature();

            if (!fluid.isSource(fluid.defaultFluidState())) {
                continue;
            }

            var recipeId = Powah.id("passive_heat_source/fluid/" + id.getNamespace() + "/" + id.getPath());
            result.add(new PassiveHeatSource(recipeId, null, fluid, heat));
        }

        result.sort(Comparator.comparingInt(PassiveHeatSource::heat));
        return result;
    }
}
