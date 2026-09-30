package owmii.powah.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import owmii.powah.lib.datamap.DataMapType;
import owmii.powah.Powah;

public record PassiveHeatSourceConfig(int temperature) {
    public static final Codec<PassiveHeatSourceConfig> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    Codec.INT.fieldOf("temperature").forGetter(PassiveHeatSourceConfig::temperature))
            .apply(builder, PassiveHeatSourceConfig::new));
    public static final DataMapType<Block, PassiveHeatSourceConfig> BLOCK_DATA_MAP = DataMapType.create(Powah.id("heat_source"), Registries.BLOCK, CODEC);
    public static final DataMapType<Fluid, PassiveHeatSourceConfig> FLUID_DATA_MAP = DataMapType.create(Powah.id("heat_source"), Registries.FLUID, CODEC);
}
