package owmii.powah.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PassiveHeatSourceConfig(int temperature) {
    public static final Codec<PassiveHeatSourceConfig> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    Codec.INT.fieldOf("temperature").forGetter(PassiveHeatSourceConfig::temperature))
            .apply(builder, PassiveHeatSourceConfig::new));
}
