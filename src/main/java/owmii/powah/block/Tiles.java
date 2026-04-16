package owmii.powah.block;

import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import owmii.powah.Powah;
import owmii.powah.block.cable.CableTile;
import owmii.powah.block.discharger.EnergyDischargerTile;
import owmii.powah.block.ender.EnderCellTile;
import owmii.powah.block.ender.EnderGateTile;
import owmii.powah.block.energizing.EnergizingOrbTile;
import owmii.powah.block.energizing.EnergizingRodTile;
import owmii.powah.block.energycell.EnergyCellTile;
import owmii.powah.block.furnator.FurnatorTile;
import owmii.powah.block.hopper.EnergyHopperTile;
import owmii.powah.block.magmator.MagmatorTile;
import owmii.powah.block.reactor.ReactorPartTile;
import owmii.powah.block.reactor.ReactorTile;
import owmii.powah.block.solar.SolarTile;
import owmii.powah.block.thermo.ThermoTile;
import owmii.powah.block.transmitter.PlayerTransmitterTile;

public class Tiles {

    private static <BE extends BlockEntity> BlockEntityType<BE> register(String path, BlockEntityType.BlockEntitySupplier<BE> supplier,
            List<Block> blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, path),
                BlockEntityType.Builder.of(supplier, blocks.toArray(Block[]::new)).build(null));
    }

    public static final BlockEntityType<EnergyCellTile> ENERGY_CELL = register("energy_cell", EnergyCellTile::new, Blcks.ENERGY_CELL.getAll());
    public static final BlockEntityType<EnderCellTile> ENDER_CELL = register("ender_cell", EnderCellTile::new, Blcks.ENDER_CELL.getAll());
    public static final BlockEntityType<EnderGateTile> ENDER_GATE = register("ender_gate", EnderGateTile::new, Blcks.ENDER_GATE.getAll());
    public static final BlockEntityType<CableTile> CABLE = register("energy_cable",
            (pos, state) -> new CableTile(pos, state, Tier.STARTER), Blcks.ENERGY_CABLE.getAll());
    public static final BlockEntityType<EnergizingOrbTile> ENERGIZING_ORB = register("energizing_orb", EnergizingOrbTile::new,
            List.of(Blcks.ENERGIZING_ORB));
    public static final BlockEntityType<EnergizingRodTile> ENERGIZING_ROD = register("energizing_rod", EnergizingRodTile::new,
            Blcks.ENERGIZING_ROD.getAll());
    public static final BlockEntityType<SolarTile> SOLAR_PANEL = register("solar_panel", SolarTile::new, Blcks.SOLAR_PANEL.getAll());
    public static final BlockEntityType<FurnatorTile> FURNATOR = register("furnator", FurnatorTile::new, Blcks.FURNATOR.getAll());
    public static final BlockEntityType<MagmatorTile> MAGMATOR = register("magmator", MagmatorTile::new, Blcks.MAGMATOR.getAll());
    public static final BlockEntityType<ThermoTile> THERMO_GEN = register("thermo_gen", ThermoTile::new, Blcks.THERMO_GENERATOR.getAll());
    public static final BlockEntityType<ReactorTile> REACTOR = register("reactor", ReactorTile::new, Blcks.REACTOR.getAll());
    public static final BlockEntityType<ReactorPartTile> REACTOR_PART = register("reactor_part", ReactorPartTile::new, Blcks.REACTOR.getAll());
    public static final BlockEntityType<PlayerTransmitterTile> PLAYER_TRANSMITTER = register("player_transmitter",
            PlayerTransmitterTile::new, Blcks.PLAYER_TRANSMITTER.getAll());
    public static final BlockEntityType<EnergyHopperTile> ENERGY_HOPPER = register("energy_hopper", EnergyHopperTile::new,
            Blcks.ENERGY_HOPPER.getAll());
    public static final BlockEntityType<EnergyDischargerTile> ENERGY_DISCHARGER = register("energy_discharger", EnergyDischargerTile::new,
            Blcks.ENERGY_DISCHARGER.getAll());

    public static void init() {
        // Called to trigger class loading and static registration
    }
}
