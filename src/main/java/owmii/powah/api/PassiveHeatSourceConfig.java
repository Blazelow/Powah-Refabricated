package owmii.powah.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

import owmii.powah.Powah;

public record PassiveHeatSourceConfig(int temperature) {
    public static final Codec<PassiveHeatSourceConfig> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    Codec.INT.fieldOf("temperature").forGetter(PassiveHeatSourceConfig::temperature))
            .apply(builder, PassiveHeatSourceConfig::new));
            .builder(Powah.id("heat_source"), Registries.BLOCK, CODEC)
            .synced(CODEC, true)
            .build();
            .builder(Powah.id("heat_source"), Registries.FLUID, CODEC)
            .synced(CODEC, true)
            .build();
}
