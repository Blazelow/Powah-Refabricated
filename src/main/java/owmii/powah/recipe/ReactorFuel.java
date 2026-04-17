package owmii.powah.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ReactorFuel(int fuelAmount, int temperature) {
    public static final Codec<ReactorFuel> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("fuelAmount").forGetter(ReactorFuel::fuelAmount),
            Codec.INT.fieldOf("temperature").forGetter(ReactorFuel::temperature)).apply(builder, ReactorFuel::new));
}
