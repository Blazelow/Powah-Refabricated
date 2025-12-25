package owmii.powah.data;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import owmii.powah.Powah;
import owmii.powah.compat.curios.CurioTagsProvider;
import owmii.powah.world.gen.Features;

public class PowahDataGenerator {
    public static void gatherData(GatherDataEvent.Client event) {
        var generator = event.getGenerator();
        var pack = generator.getVanillaPack(true);

        var registries = event.getLookupProvider();

        var blockTagsProvider = pack
                .addProvider(packOutput -> new TagsProvider.Blocks(packOutput, registries));
        pack.addProvider(
                packOutput -> new TagsProvider.Items(packOutput, registries, blockTagsProvider.contentsGetter()));

        pack.addProvider(packOutput -> new CurioTagsProvider(packOutput, registries));
        pack.addProvider(packOutput -> new RecipeProvider.Runner(packOutput, registries));
        pack.addProvider(packOutput -> createLoot(packOutput, registries));
        pack.addProvider(packOutput -> new PowahDataMapProvider(packOutput, registries));

        var worldgenBuilder = new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, Features::initConfiguredFeatures)
                .add(Registries.PLACED_FEATURE, Features::initPlacedFeatures)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Features::initBiomeModifiers);
        pack.addProvider(output -> new DatapackBuiltinEntriesProvider(output, registries, worldgenBuilder, Set.of(Powah.MOD_ID)));
    }

    public static LootTableProvider createLoot(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(LootTableGenerator::new, LootContextParamSets.BLOCK)),
                registries);
    }
}
