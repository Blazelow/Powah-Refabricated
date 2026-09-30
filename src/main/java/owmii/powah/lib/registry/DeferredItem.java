package owmii.powah.lib.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class DeferredItem<T extends Item> extends DeferredHolder<Item, T> implements ItemLike {
    DeferredItem(ResourceLocation id) {
        super(Registries.ITEM, id);
    }

    @Override
    public Item asItem() {
        return get();
    }
}
