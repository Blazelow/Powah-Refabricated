package owmii.powah.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;
import owmii.powah.Powah;

public record ReactorFuel(int fuelAmount, int temperature) {
    private static final Codec<ReactorFuel> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("fuelAmount").forGetter(ReactorFuel::fuelAmount),
            Codec.INT.fieldOf("temperature").forGetter(ReactorFuel::temperature)).apply(builder, ReactorFuel::new));

    @Nullable
}
