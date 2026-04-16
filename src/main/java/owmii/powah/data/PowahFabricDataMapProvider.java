package owmii.powah.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.JsonOps;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import owmii.powah.Powah;
import owmii.powah.api.FluidCoolantConfig;
import owmii.powah.api.MagmatorFuelValue;
import owmii.powah.api.PassiveHeatSourceConfig;
import owmii.powah.api.SolidCoolantConfig;
import owmii.powah.block.Blcks;
import owmii.powah.item.Itms;
import owmii.powah.recipe.ReactorFuel;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Generates JSON data files for PowahDataLoader (replaces NeoForge DataMapProvider).
 * Output: data/powah/powah_data/<type>/<id>.json
 */
public class PowahFabricDataMapProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final FabricDataOutput output;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public PowahFabricDataMapProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.output = output;
        this.registries = registries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        var futures = new java.util.ArrayList<CompletableFuture<?>>();

        // Reactor fuel
        futures.add(write(cache, "reactor_fuel", Powah.id("uraninite"),
                ReactorFuel.CODEC, new ReactorFuel(100, 700)));

        // Fluid coolant
        futures.add(write(cache, "fluid_coolant",
                ResourceLocation.fromNamespaceAndPath("minecraft", "water"),
                FluidCoolantConfig.CODEC, new FluidCoolantConfig(0)));

        // Solid coolants
        futures.add(write(cache, "solid_coolant", ResourceLocation.fromNamespaceAndPath("minecraft", "snow_block"),
                SolidCoolantConfig.CODEC, new SolidCoolantConfig(48, -3)));
        futures.add(write(cache, "solid_coolant", ResourceLocation.fromNamespaceAndPath("minecraft", "snowball"),
                SolidCoolantConfig.CODEC, new SolidCoolantConfig(12, -3)));
        futures.add(write(cache, "solid_coolant", ResourceLocation.fromNamespaceAndPath("minecraft", "ice"),
                SolidCoolantConfig.CODEC, new SolidCoolantConfig(48, -5)));
        futures.add(write(cache, "solid_coolant", ResourceLocation.fromNamespaceAndPath("minecraft", "packed_ice"),
                SolidCoolantConfig.CODEC, new SolidCoolantConfig(192, -8)));
        futures.add(write(cache, "solid_coolant", ResourceLocation.fromNamespaceAndPath("minecraft", "blue_ice"),
                SolidCoolantConfig.CODEC, new SolidCoolantConfig(568, -17)));
        futures.add(write(cache, "solid_coolant", Powah.id("dry_ice"),
                SolidCoolantConfig.CODEC, new SolidCoolantConfig(712, -32)));

        // Magmator fuel (lava)
        futures.add(write(cache, "magmator_fuel", ResourceLocation.fromNamespaceAndPath("minecraft", "lava"),
                MagmatorFuelValue.CODEC, new MagmatorFuelValue(10000)));

        // Passive fluid heat sources
        futures.add(write(cache, "fluid_heat_source", ResourceLocation.fromNamespaceAndPath("minecraft", "lava"),
                PassiveHeatSourceConfig.CODEC, new PassiveHeatSourceConfig(1000)));

        // Passive block heat sources
        futures.add(write(cache, "block_heat_source", ResourceLocation.fromNamespaceAndPath("minecraft", "magma_block"),
                PassiveHeatSourceConfig.CODEC, new PassiveHeatSourceConfig(800)));
        futures.add(write(cache, "block_heat_source", Powah.id("blazing_crystal_block"),
                PassiveHeatSourceConfig.CODEC, new PassiveHeatSourceConfig(2800)));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private <T> CompletableFuture<?> write(CachedOutput cache, String type, ResourceLocation id,
            com.mojang.serialization.Codec<T> codec, T value) {
        return registries.thenCompose(regs -> {
            var json = codec.encodeStart(JsonOps.INSTANCE, value)
                    .result()
                    .map(GSON::toJsonTree)
                    .orElseThrow();
            Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
                    .resolve(id.getNamespace())
                    .resolve("powah_data")
                    .resolve(type)
                    .resolve(id.getPath() + ".json");
            return DataProvider.saveStable(cache, json, path);
        });
    }

    @Override
    public String getName() {
        return "Powah Data Maps";
    }
}
