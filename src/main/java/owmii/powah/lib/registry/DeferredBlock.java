package owmii.powah.lib.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

public class DeferredBlock<T extends Block> extends DeferredHolder<Block, T> implements ItemLike {
    DeferredBlock(Identifier id) {
        super(Registries.BLOCK, id);
    }

    @Override
    public Item asItem() {
        return get().asItem();
    }
}
