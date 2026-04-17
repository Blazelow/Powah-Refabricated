package owmii.powah.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SolidCoolantConfig(int amount, int temperature) {
    public static final Codec<SolidCoolantConfig> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    Codec.INT.fieldOf("amount").forGetter(SolidCoolantConfig::amount),
                    Codec.INT.fieldOf("temperature").forGetter(SolidCoolantConfig::temperature))
            .apply(builder, SolidCoolantConfig::new));
}
