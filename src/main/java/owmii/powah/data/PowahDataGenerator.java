package owmii.powah.data;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import owmii.powah.Powah;
import owmii.powah.compat.trinkets.TrinketsTagsProvider;
import owmii.powah.world.gen.Features;

public class PowahDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        var blockTagsProvider = pack.addProvider(TagsProvider.Blocks::new);
        pack.addProvider((output, registries) ->
                new TagsProvider.Items(output, registries, blockTagsProvider.contentsGetter()));
        pack.addProvider(RecipeProvider::new);
        pack.addProvider((output, registries) -> createLoot(output, registries));
        pack.addProvider(PowahFabricDataMapProvider::new);
        pack.addProvider(TrinketsTagsProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        registryBuilder
                .add(Registries.CONFIGURED_FEATURE, Features::initConfiguredFeatures)
                .add(Registries.PLACED_FEATURE, Features::initPlacedFeatures);
    }

    public static LootTableProvider createLoot(net.fabricmc.fabric.api.datagen.v1.FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registries) {
        return new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(LootTableGenerator::new, LootContextParamSets.BLOCK)),
                registries);
    }
}
