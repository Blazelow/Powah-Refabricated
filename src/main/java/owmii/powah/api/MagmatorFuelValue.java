package owmii.powah.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.material.Fluid;

import owmii.powah.Powah;

/**
 * Fuel for the {@link owmii.powah.block.magmator.MagmatorTile}.
 *
 * @param energyProduced FE per 100mb of fluid
 */
public record MagmatorFuelValue(int energyProduced) {
    public static final Codec<MagmatorFuelValue> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(
                    Codec.INT.fieldOf("energy_produced").forGetter(MagmatorFuelValue::energyProduced))
            .apply(builder, MagmatorFuelValue::new));

            .synced(CODEC, true)
            .build();
}
