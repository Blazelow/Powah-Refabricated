package owmii.powah.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import owmii.powah.Powah;
import owmii.powah.block.cable.CableBlock;
import owmii.powah.block.discharger.EnergyDischargerBlock;
import owmii.powah.block.ender.EnderCellBlock;
import owmii.powah.block.ender.EnderGateBlock;
import owmii.powah.block.energizing.EnergizingOrbBlock;
import owmii.powah.block.energizing.EnergizingRodBlock;
import owmii.powah.block.energycell.EnergyCellBlock;
import owmii.powah.block.furnator.FurnatorBlock;
import owmii.powah.block.hopper.EnergyHopperBlock;
import owmii.powah.block.magmator.MagmatorBlock;
import owmii.powah.block.reactor.ReactorBlock;
import owmii.powah.block.solar.SolarBlock;
import owmii.powah.block.thermo.ThermoBlock;
import owmii.powah.block.transmitter.PlayerTransmitterBlock;
import owmii.powah.lib.block.Properties;
import owmii.powah.lib.registry.VarReg;

public class Blcks {

    public static final VarReg<Tier, Block> ENERGY_CELL = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "energy_cell",
            variant -> new EnergyCellBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.values());
    public static final VarReg<Tier, Block> ENDER_CELL = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "ender_cell",
            variant -> new EnderCellBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> ENERGY_CABLE = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "energy_cable",
            variant -> new CableBlock(Properties.metalNoSolid(2.0f, 20.0f).noCollission(), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> ENDER_GATE = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "ender_gate",
            variant -> new EnderGateBlock(Properties.metalNoSolid(2.0f, 20.0f).noCollission(), variant), Tier.getNormalVariants());
    public static final Block ENERGIZING_ORB = register("energizing_orb", new EnergizingOrbBlock(Properties.metalNoSolid(2.0f, 20.0f)));
    public static final VarReg<Tier, Block> ENERGIZING_ROD = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "energizing_rod",
            variant -> new EnergizingRodBlock(Properties.metalNoSolid(2.0f, 20.0f).noCollission(), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> FURNATOR = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "furnator",
            variant -> new FurnatorBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> MAGMATOR = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "magmator",
            variant -> new MagmatorBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> THERMO_GENERATOR = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "thermo_generator",
            variant -> new ThermoBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> SOLAR_PANEL = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "solar_panel",
            variant -> new SolarBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> REACTOR = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "reactor",
            variant -> new ReactorBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> PLAYER_TRANSMITTER = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "player_transmitter",
            variant -> new PlayerTransmitterBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> ENERGY_HOPPER = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "energy_hopper",
            variant -> new EnergyHopperBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.getNormalVariants());
    public static final VarReg<Tier, Block> ENERGY_DISCHARGER = new VarReg<>(BuiltInRegistries.BLOCK, Powah.MOD_ID, "energy_discharger",
            variant -> new EnergyDischargerBlock(Properties.metalNoSolid(2.0f, 20.0f), variant), Tier.getNormalVariants());
    public static final Block ENERGIZED_STEEL = register("energized_steel_block", new Block(Properties.metal(2.0f, 20.0f)));
    public static final Block BLAZING_CRYSTAL = register("blazing_crystal_block", new Block(Properties.metal(2.0f, 20.0f)));
    public static final Block NIOTIC_CRYSTAL = register("niotic_crystal_block", new Block(Properties.metal(2.0f, 20.0f)));
    public static final Block SPIRITED_CRYSTAL = register("spirited_crystal_block", new Block(Properties.metal(2.0f, 20.0f)));
    public static final Block NITRO_CRYSTAL = register("nitro_crystal_block", new Block(Properties.metal(2.0f, 20.0f)));
    public static final Block URANINITE = register("uraninite_block", new Block(Properties.metal(2.0f, 20.0f)));
    public static final Block DEEPSLATE_URANINITE_ORE_POOR = register("deepslate_uraninite_ore_poor",
            new DropExperienceBlock(ConstantInt.of(0), Properties.deepslate()));
    public static final Block DEEPSLATE_URANINITE_ORE = register("deepslate_uraninite_ore",
            new DropExperienceBlock(ConstantInt.of(0), Properties.deepslate()));
    public static final Block DEEPSLATE_URANINITE_ORE_DENSE = register("deepslate_uraninite_ore_dense",
            new DropExperienceBlock(ConstantInt.of(0), Properties.deepslate()));
    public static final Block URANINITE_ORE_POOR = register("uraninite_ore_poor",
            new DropExperienceBlock(ConstantInt.of(0), Properties.rock(3.0f, 8.0f)));
    public static final Block URANINITE_ORE = register("uraninite_ore",
            new DropExperienceBlock(ConstantInt.of(0), Properties.rock(3.2f, 8.0f)));
    public static final Block URANINITE_ORE_DENSE = register("uraninite_ore_dense",
            new DropExperienceBlock(ConstantInt.of(0), Properties.rock(4.0f, 8.0f)));
    public static final Block DRY_ICE = register("dry_ice", new Block(Properties.rock(2.0f, 8.0f)));

    private static <T extends Block> T register(String name, T block) {
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, name), block);
    }

    public static void init() {
        // Called to trigger class loading and static block registration
    }
}
