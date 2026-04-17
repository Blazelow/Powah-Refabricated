package owmii.powah;

import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import owmii.powah.block.Blcks;
import owmii.powah.block.Tiles;
import owmii.powah.block.cable.CableNet;
import owmii.powah.components.PowahComponents;
import owmii.powah.config.v2.PowahConfig;
import owmii.powah.data.PowahDataLoader;
import owmii.powah.entity.Entities;
import owmii.powah.inventory.Containers;
import owmii.powah.item.CreativeTabs;
import owmii.powah.item.Itms;
import owmii.powah.lib.block.AbstractEnergyStorage;
import owmii.powah.lib.block.IBlock;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.ITankHolder;
import owmii.powah.lib.item.IEnergyContainingItem;
import owmii.powah.lib.item.ItemBlock;
import owmii.powah.lib.logistics.energy.Energy;
import owmii.powah.network.Network;
import owmii.powah.recipe.ReactorFuel;
import owmii.powah.recipe.Recipes;
import owmii.powah.util.Wrench;
import owmii.powah.compat.trinkets.TrinketsCompat;
import owmii.powah.world.gen.Features;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import team.reborn.energy.api.EnergyStorage;

@SuppressWarnings("UnstableApiUsage")
public class Powah implements ModInitializer {
    public static final String MOD_ID = "powah";
    private static final ConfigHolder<PowahConfig> CONFIG = PowahConfig.register();
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static PowahConfig config() {
        return CONFIG.getConfig();
    }

    @Override
    public void onInitialize() {
        // Trigger static registration of all registries (order matters: blocks first)
        Blcks.init();
        registerBlockItems();
        Tiles.init();
        Itms.init();
        Containers.init();
        Entities.init();
        Recipes.init();
        CreativeTabs.init();
        PowahComponents.init();

        Network.registerServer();
        PowahDataLoader.register();

        registerCapabilities();

        // Use block (right-click) event for wrench
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (Wrench.removeWithWrench(player, world, hand, hitResult)) {
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
            return InteractionResult.PASS;
        });

        // Chunk unload: clean up cable networks
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_UNLOAD.register(
                (world, chunk) -> CableNet.removeChunk(world, chunk));

        // World gen
        // Trinkets compat - charge batteries worn in trinket slots
        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            TrinketsCompat.init();
        }

        Features.registerBiomeModifications();
    }

    private void registerBlockItems() {
        for (var entry : BuiltInRegistries.BLOCK.entrySet()) {
            var id = entry.getKey();
            if (id.location().getNamespace().equals(MOD_ID)) {
                var block = entry.getValue();
                BlockItem blockItem;
                if (block instanceof IBlock<?, ?> iBlock) {
                    blockItem = iBlock.getBlockItem(new Item.Properties(), CreativeTabs.MAIN_KEY);
                } else {
                    blockItem = new ItemBlock<>(block, new Item.Properties(), CreativeTabs.MAIN_KEY);
                }
                Registry.register(BuiltInRegistries.ITEM, id.location(), blockItem);
            }
        }
    }

    private void registerCapabilities() {
        // Reactor part: delegate capability lookups to the core tile
        EnergyStorage.SIDED.registerForBlockEntity(
                (reactorPart, side) -> reactorPart.isExtractor() ? reactorPart.getCoreEnergyStorage() : null,
                Tiles.REACTOR_PART);
        ItemStorage.SIDED.registerForBlockEntity(
                (reactorPart, side) -> reactorPart.getCoreItemHandler(),
                Tiles.REACTOR_PART);
        net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.SIDED.registerForBlockEntity(
                (reactorPart, side) -> reactorPart.getCoreFluidHandler(),
                Tiles.REACTOR_PART);

        // All other block entities
        for (var entry : BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet()) {
            if (!entry.getKey().location().getNamespace().equals(MOD_ID)) continue;
            var beType = (BlockEntityType<?>) entry.getValue();
            if (beType == Tiles.REACTOR_PART) continue; // already handled above

            // Create a dummy BE to check what interfaces it implements
            var validBlock = beType.getValidBlocks().stream().iterator().next();
            var dummyBe = beType.create(BlockPos.ZERO, validBlock.defaultBlockState());
            if (dummyBe == null) continue;

            registerBlockEntityCapabilities(beType, dummyBe.getClass());
        }

        // Energy-containing items
        for (var item : BuiltInRegistries.ITEM) {
            if (item instanceof IEnergyContainingItem eci) {
                EnergyStorage.ITEM.registerForItems((stack, ctx) -> {
                    var info = eci.getEnergyInfo();
                    if (info == null) return null;
                    return new Energy.Item(stack, info).createItemCapability();
                }, item);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <BE extends net.minecraft.world.level.block.entity.BlockEntity>
            void registerBlockEntityCapabilities(BlockEntityType<BE> beType, Class<?> beClass) {
        if (AbstractEnergyStorage.class.isAssignableFrom(beClass)) {
            EnergyStorage.SIDED.registerForBlockEntity((be, side) -> {
                var energyStorage = (AbstractEnergyStorage<?, ?>) be;
                return energyStorage.getExternalStorage(side);
            }, beType);
        }
        if (IInventoryHolder.class.isAssignableFrom(beClass)) {
            ItemStorage.SIDED.registerForBlockEntity((be, side) -> {
                var inv = ((IInventoryHolder) be).getInventory();
                return inv.asFabricStorage();
            }, beType);
        }
        if (ITankHolder.class.isAssignableFrom(beClass)) {
            net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.SIDED.registerForBlockEntity(
                    (be, side) -> ((ITankHolder) be).getTank(), beType);
        }
    }
}
