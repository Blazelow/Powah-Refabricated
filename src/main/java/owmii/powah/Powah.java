package owmii.powah;

import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
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

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (Wrench.removeWithWrench(player, world, hand, hitResult)) {
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
            return InteractionResult.PASS;
        });

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents.CHUNK_UNLOAD.register(
                (world, chunk) -> CableNet.removeChunk(world, chunk));

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
        // ---- Reactor part ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.isExtractor() ? be.getCoreEnergyStorage() : null, Tiles.REACTOR_PART);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getCoreItemHandler(), Tiles.REACTOR_PART);
        FluidStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getCoreFluidHandler(), Tiles.REACTOR_PART);

        // ---- Energy Cell ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.ENERGY_CELL);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.ENERGY_CELL);

        // ---- Ender Cell ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.ENDER_CELL);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.ENDER_CELL);

        // ---- Ender Gate ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.ENDER_GATE);

        // ---- Cable ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.CABLE);

        // ---- Energizing Rod ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.ENERGIZING_ROD);

        // ---- Energizing Orb (inventory only, no energy storage) ----
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.ENERGIZING_ORB);

        // ---- Solar Panel ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.SOLAR_PANEL);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.SOLAR_PANEL);

        // ---- Furnator ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.FURNATOR);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.FURNATOR);

        // ---- Magmator ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.MAGMATOR);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.MAGMATOR);
        FluidStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getTank(), Tiles.MAGMATOR);

        // ---- Thermo Generator ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.THERMO_GEN);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.THERMO_GEN);
        FluidStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getTank(), Tiles.THERMO_GEN);

        // ---- Reactor ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.REACTOR);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.REACTOR);
        FluidStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getTank(), Tiles.REACTOR);

        // ---- Player Transmitter ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.PLAYER_TRANSMITTER);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.PLAYER_TRANSMITTER);

        // ---- Energy Hopper ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.ENERGY_HOPPER);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.ENERGY_HOPPER);

        // ---- Energy Discharger ----
        EnergyStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getExternalStorage(side), Tiles.ENERGY_DISCHARGER);
        ItemStorage.SIDED.registerForBlockEntity(
                (be, side) -> be.getInventory().asFabricStorage(), Tiles.ENERGY_DISCHARGER);

        // ---- Energy-containing items ----
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
}
