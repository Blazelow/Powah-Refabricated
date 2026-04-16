package owmii.powah.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import owmii.powah.lib.item.Stacks;

public class Stack {
    public static Stacks copy(Stacks stacks) {
        Stacks itemStacks = Stacks.create();
        stacks.forEach(stack -> itemStacks.add(stack.copy()));
        return itemStacks;
    }

    public static boolean equals(ItemStack stack, ItemStack other) {
        return !stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, other);
    }

    public static String path(ItemStack provider) {
        return location(provider).getPath();
    }

    public static String modId(ItemStack provider) {
        return location(provider).getNamespace();
    }

    public static ResourceLocation location(ItemStack stack) {
        return location(stack.getItem());
    }

    public static String path(ItemLike provider) {
        return location(provider).getPath();
    }

    public static String modId(ItemLike provider) {
        return location(provider).getNamespace();
    }

    @SuppressWarnings("ConstantConditions")
    public static ResourceLocation location(ItemLike provider) {
        return BuiltInRegistries.ITEM.getKey(provider.asItem());
    }
}
