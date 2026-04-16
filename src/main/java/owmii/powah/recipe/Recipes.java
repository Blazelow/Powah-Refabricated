package owmii.powah.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import owmii.powah.Powah;
import owmii.powah.block.energizing.EnergizingRecipe;

public class Recipes {

    public static final EnergizingRecipe.Serializer ENERGIZING_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, "energizing"),
            new EnergizingRecipe.Serializer());

    public static final RecipeType<EnergizingRecipe> ENERGIZING = Registry.register(
            BuiltInRegistries.RECIPE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, "energizing"),
            new RecipeType<>() {
                public String toString() {
                    return EnergizingRecipe.ID.toString();
                }
            });

    public static void init() {
        // Called to trigger class loading and static registration
    }
}
