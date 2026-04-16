package owmii.powah.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.material.Fluid;

import owmii.powah.Powah;

public record FluidCoolantConfig(int temperature) {
    public static final Codec<FluidCoolantConfig> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    Codec.intRange(-273, 0).fieldOf("temperature").forGetter(FluidCoolantConfig::temperature))
            .apply(builder, FluidCoolantConfig::new));
            .synced(CODEC, true)
            .build();

    public FluidCoolantConfig {
        if (temperature > 0) {
            throw new IllegalArgumentException("Temperature must be less than or equal to 0.");
        }
        if (temperature < -273) {
            throw new IllegalArgumentException("Temperature must be greater than or equal to -273.");
        }
    }
}
