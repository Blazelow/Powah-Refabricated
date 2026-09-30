package owmii.powah.lib.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class DeferredRegister<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    protected final String namespace;
    private final List<Entry<T>> pending = new ArrayList<>();
    private final List<DeferredHolder<T, ? extends T>> entries = new ArrayList<>();

    private record Entry<T>(DeferredHolder<T, ? extends T> holder, Supplier<? extends T> supplier) {
    }

    protected DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        this.registryKey = registryKey;
        this.namespace = namespace;
    }

    public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        return new DeferredRegister<>(registryKey, namespace);
    }

    public static Blocks createBlocks(String namespace) {
        return new Blocks(namespace);
    }

    public static Items createItems(String namespace) {
        return new Items(namespace);
    }

    public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> supplier) {
        var holder = new DeferredHolder<T, I>(registryKey, Identifier.fromNamespaceAndPath(namespace, name));
        add(holder, supplier);
        return holder;
    }

    protected <I extends T> void add(DeferredHolder<T, I> holder, Supplier<? extends I> supplier) {
        pending.add(new Entry<>(holder, supplier));
        entries.add(holder);
    }

    public Collection<DeferredHolder<T, ? extends T>> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    @SuppressWarnings("unchecked")
    public void register() {
        Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.getValue(registryKey.identifier());
        for (var entry : pending) {
            bindAndRegister(registry, entry);
        }
        pending.clear();
    }

    @SuppressWarnings("unchecked")
    private static <T, I extends T> void bindAndRegister(Registry<T> registry, Entry<T> entry) {
        var holder = (DeferredHolder<T, I>) entry.holder();
        I value = ((Supplier<? extends I>) entry.supplier()).get();
        Registry.register(registry, holder.getId(), value);
        holder.bind(value);
    }

    public static class Blocks extends DeferredRegister<Block> {
        protected Blocks(String namespace) {
            super(Registries.BLOCK, namespace);
        }

        @Override
        public <B extends Block> DeferredBlock<B> register(String name, Supplier<? extends B> supplier) {
            var holder = new DeferredBlock<B>(Identifier.fromNamespaceAndPath(this.namespace, name));
            add(holder, supplier);
            return holder;
        }

        public <B extends Block> DeferredBlock<B> registerBlock(String name, Function<BlockBehaviour.Properties, ? extends B> factory) {
            var id = Identifier.fromNamespaceAndPath(this.namespace, name);
            return register(name, () -> factory.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id))));
        }
    }

    public static class Items extends DeferredRegister<Item> {
        protected Items(String namespace) {
            super(Registries.ITEM, namespace);
        }

        @Override
        public <I extends Item> DeferredItem<I> register(String name, Supplier<? extends I> supplier) {
            var holder = new DeferredItem<I>(Identifier.fromNamespaceAndPath(this.namespace, name));
            add(holder, supplier);
            return holder;
        }

        public <I extends Item> DeferredItem<I> registerItem(String name, Function<Item.Properties, ? extends I> factory) {
            var id = Identifier.fromNamespaceAndPath(this.namespace, name);
            return register(name, () -> factory.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id))));
        }
    }
}
