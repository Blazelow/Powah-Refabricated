package owmii.powah.compat.trinkets;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;

// Trinkets compat is disabled - no tags generated.
public class TrinketsTagsProvider extends FabricTagProvider.ItemTagProvider {
    public TrinketsTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {}

    @Override
    public String getName() { return "Powah Trinkets Tags"; }
}
