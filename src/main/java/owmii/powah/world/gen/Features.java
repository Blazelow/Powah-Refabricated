package owmii.powah.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import owmii.powah.Powah;

public class Features {
    private static final TagKey<Biome> DRY_ICE_BIOME = TagKey.create(Registries.BIOME, Powah.id("has_dry_ice"));

    public static final ResourceKey<PlacedFeature> PLACED_DRY_ICE = ResourceKey.create(Registries.PLACED_FEATURE, Powah.id("dry_ice"));
    public static final ResourceKey<PlacedFeature> PLACED_URANINITE_POOR = ResourceKey.create(Registries.PLACED_FEATURE,
            Powah.id("uraninite_ore_poor"));
    public static final ResourceKey<PlacedFeature> PLACED_URANINITE = ResourceKey.create(Registries.PLACED_FEATURE, Powah.id("uraninite_ore"));
    public static final ResourceKey<PlacedFeature> PLACED_URANINITE_DENSE = ResourceKey.create(Registries.PLACED_FEATURE,
            Powah.id("uraninite_ore_dense"));

    public static void register() {
        var overworld = BiomeSelectors.foundInOverworld();
        BiomeModifications.addFeature(overworld, GenerationStep.Decoration.UNDERGROUND_ORES, PLACED_URANINITE_POOR);
        BiomeModifications.addFeature(overworld, GenerationStep.Decoration.UNDERGROUND_ORES, PLACED_URANINITE);
        BiomeModifications.addFeature(overworld, GenerationStep.Decoration.UNDERGROUND_ORES, PLACED_URANINITE_DENSE);
        BiomeModifications.addFeature(BiomeSelectors.tag(DRY_ICE_BIOME), GenerationStep.Decoration.UNDERGROUND_ORES, PLACED_DRY_ICE);
    }
}
