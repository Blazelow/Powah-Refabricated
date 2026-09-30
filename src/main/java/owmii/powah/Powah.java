package owmii.powah;

import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import owmii.powah.api.FluidCoolantConfig;
import owmii.powah.api.MagmatorFuelValue;
import owmii.powah.api.PassiveHeatSourceConfig;
import owmii.powah.api.SolidCoolantConfig;
import owmii.powah.block.Blcks;
import owmii.powah.block.Tiles;
import owmii.powah.block.cable.CableNet;
import owmii.powah.block.reactor.ReactorPartTile;
import owmii.powah.block.reactor.ReactorTile;
import owmii.powah.compat.trinkets.TrinketsCompat;
import owmii.powah.components.PowahComponents;
import owmii.powah.config.v2.PowahConfig;
import owmii.powah.entity.Entities;
import owmii.powah.inventory.Containers;
import owmii.powah.item.CreativeTabs;
import owmii.powah.item.Itms;
import owmii.powah.lib.block.AbstractEnergyStorage;
import owmii.powah.lib.block.IBlock;
import owmii.powah.lib.block.IInventoryHolder;
import owmii.powah.lib.block.ITankHolder;
import owmii.powah.lib.datamap.DataMaps;
import owmii.powah.lib.item.IEnergyContainingItem;
import owmii.powah.lib.item.ItemBlock;
import owmii.powah.lib.logistics.energy.ItemEnergyStorage;
import owmii.powah.network.Network;
import owmii.powah.recipe.ReactorFuel;
import owmii.powah.recipe.Recipes;
import owmii.powah.util.Wrench;
import owmii.powah.world.gen.Features;
import team.reborn.energy.api.EnergyStorage;

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
        PowahComponents.DR.register();
        Blcks.DR.register();
        registerBlockItems();
        Itms.DR.register();
        Tiles.DR.register();
        Containers.DR.register();
        Entities.DR.register();
        Recipes.DR_SERIALIZER.register();
        Recipes.DR_TYPE.register();
        CreativeTabs.DR.register();

        registerTransfer();
        Network.register();
        DataMaps.register(ReactorFuel.DATA_MAP_TYPE);
        DataMaps.register(SolidCoolantConfig.DATA_MAP_TYPE);
        DataMaps.register(PassiveHeatSourceConfig.BLOCK_DATA_MAP);
        DataMaps.register(PassiveHeatSourceConfig.FLUID_DATA_MAP);
        DataMaps.register(FluidCoolantConfig.DATA_MAP_TYPE);
        DataMaps.register(MagmatorFuelValue.DATA_MAP_TYPE);
        DataMaps.init();
        Features.register();

        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (player.isSpectator()) {
                return InteractionResult.PASS;
            }
            if (Wrench.removeWithWrench(player, level, hand, hitResult)) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        });
        ServerChunkEvents.CHUNK_UNLOAD.register(CableNet::removeChunk);

        if (FabricLoader.getInstance().isModLoaded("trinkets")) {
            TrinketsCompat.init();
        }
    }

    private void registerTransfer() {
        var reactorBlocks = Blcks.REACTOR.getAll().toArray(Block[]::new);
        EnergyStorage.SIDED.registerForBlocks((level, pos, state, be, side) -> {
            if (be instanceof ReactorPartTile part) {
                return part.isExtractor() ? part.getCoreEnergyStorage() : null;
            }
            return be instanceof ReactorTile core ? core.getExternalStorage(side) : null;
        }, reactorBlocks);
        ItemStorage.SIDED.registerForBlocks((level, pos, state, be, side) -> {
            if (be instanceof ReactorPartTile part) {
                return part.getCoreItemHandler();
            }
            return be instanceof ReactorTile core && !core.getInventory().isBlank() ? core.getInventory().getStorage() : null;
        }, reactorBlocks);
        FluidStorage.SIDED.registerForBlocks((level, pos, state, be, side) -> {
            if (be instanceof ReactorPartTile part) {
                return part.getCoreFluidHandler();
            }
            return be instanceof ReactorTile core ? core.getTank().getStorage() : null;
        }, reactorBlocks);

        for (var entry : Tiles.DR.getEntries()) {
            var type = entry.get();
            if (type == Tiles.REACTOR.get() || type == Tiles.REACTOR_PART.get()) {
                continue;
            }
            var validBlock = type.validBlocks.stream().iterator().next();
            var be = type.create(BlockPos.ZERO, validBlock.defaultBlockState());
            if (be == null) {
                throw new IllegalStateException("Failed to create a dummy BE for " + entry.getId());
            }

            registerBlockEntityLookups(type, be.getClass());
        }

        for (var entry : Itms.DR.getEntries()) {
            if (entry.get() instanceof IEnergyContainingItem eci) {
                EnergyStorage.ITEM.registerForItems((stack, context) -> {
                    var info = eci.getEnergyInfo();
                    return info == null ? null : new ItemEnergyStorage(context, info);
                }, entry.get());
            }
        }
    }

    private static void registerBlockEntityLookups(BlockEntityType<?> beType, Class<?> beClass) {
        if (AbstractEnergyStorage.class.isAssignableFrom(beClass)) {
            EnergyStorage.SIDED.registerForBlockEntity((be, side) -> ((AbstractEnergyStorage<?, ?>) be).getExternalStorage(side), beType);
        }
        if (IInventoryHolder.class.isAssignableFrom(beClass)) {
            ItemStorage.SIDED.registerForBlockEntity((be, side) -> {
                var inv = ((IInventoryHolder) be).getInventory();
                return inv.isBlank() ? null : inv.getStorage();
            }, beType);
        }
        if (ITankHolder.class.isAssignableFrom(beClass)) {
            FluidStorage.SIDED.registerForBlockEntity((be, side) -> ((ITankHolder) be).getTank().getStorage(), beType);
        }
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
}
