package owmii.powah.compat.trinkets;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import owmii.powah.item.Itms;

/**
 * Tags Powah batteries as Trinkets-compatible items.
 * Replaces CurioTagsProvider (NeoForge/Curios).
 *
 * Battery items are tagged under trinkets:all/charm/powah_battery so players
 * can equip them in any trinket charm slot.
 */
public class TrinketsTagsProvider extends FabricTagProvider.ItemTagProvider {

    /** Trinkets tag for equippable items. Format: trinkets:<group>/<slot> */
    private static final TagKey<Item> TRINKETS_ALL =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("trinkets", "all/charm/powah_battery"));

    public TrinketsTagsProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        var tag = getOrCreateTagBuilder(TRINKETS_ALL);
        Itms.BATTERY.getAll().forEach(tag::add);
    }

    @Override
    public String getName() {
        return "Powah Trinkets Tags";
    }
}
