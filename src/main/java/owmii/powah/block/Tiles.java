package owmii.powah.block;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import owmii.powah.Powah;
import owmii.powah.block.cable.CableBlockEntity;
import owmii.powah.block.discharger.EnergyDischargerBlockEntity;
import owmii.powah.block.ender.EnderCellBlockEntity;
import owmii.powah.block.ender.EnderGateBlockEntity;
import owmii.powah.block.energizing.EnergizingOrbBlockEntity;
import owmii.powah.block.energizing.EnergizingRodBlockEntity;
import owmii.powah.block.energycell.EnergyCellBlockEntity;
import owmii.powah.block.furnator.FurnatorBlockEntity;
import owmii.powah.block.hopper.EnergyHopperBlockEntity;
import owmii.powah.block.magmator.MagmatorBlockEntity;
import owmii.powah.block.reactor.ReactorBlockEntity;
import owmii.powah.block.reactor.ReactorPartBlockEntity;
import owmii.powah.block.solar.SolarBlockEntity;
import owmii.powah.block.thermo.ThermoBlockEntity;
import owmii.powah.block.transmitter.PlayerTransmitterBlockEntity;

public class Tiles {
    public static final DeferredRegister<BlockEntityType<?>> DR = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Powah.MOD_ID);

    private static <BE extends BlockEntity> Supplier<BlockEntityType<BE>> register(String path, BlockEntityType.BlockEntitySupplier<BE> supplier,
            Supplier<List<Block>> blocks) {
        return DR.register(path, () -> new BlockEntityType<>(supplier, blocks.get().toArray(Block[]::new)));
    }

    public static final Supplier<BlockEntityType<EnergyCellBlockEntity>> ENERGY_CELL = register("energy_cell", EnergyCellBlockEntity::new,
            () -> Blcks.ENERGY_CELL.getAll());
    public static final Supplier<BlockEntityType<EnderCellBlockEntity>> ENDER_CELL = register("ender_cell", EnderCellBlockEntity::new,
            () -> Blcks.ENDER_CELL.getAll());
    public static final Supplier<BlockEntityType<EnderGateBlockEntity>> ENDER_GATE = register("ender_gate", EnderGateBlockEntity::new,
            () -> Blcks.ENDER_GATE.getAll());
    public static final Supplier<BlockEntityType<CableBlockEntity>> CABLE = register("energy_cable",
            (pos, state) -> new CableBlockEntity(pos, state, Tier.STARTER), () -> Blcks.ENERGY_CABLE.getAll());
    public static final Supplier<BlockEntityType<EnergizingOrbBlockEntity>> ENERGIZING_ORB = register("energizing_orb", EnergizingOrbBlockEntity::new,
            () -> List.of(Blcks.ENERGIZING_ORB.get()));
    public static final Supplier<BlockEntityType<EnergizingRodBlockEntity>> ENERGIZING_ROD = register("energizing_rod", EnergizingRodBlockEntity::new,
            () -> Blcks.ENERGIZING_ROD.getAll());
    public static final Supplier<BlockEntityType<SolarBlockEntity>> SOLAR_PANEL = register("solar_panel", SolarBlockEntity::new,
            () -> Blcks.SOLAR_PANEL.getAll());
    public static final Supplier<BlockEntityType<FurnatorBlockEntity>> FURNATOR = register("furnator", FurnatorBlockEntity::new,
            () -> Blcks.FURNATOR.getAll());
    public static final Supplier<BlockEntityType<MagmatorBlockEntity>> MAGMATOR = register("magmator", MagmatorBlockEntity::new,
            () -> Blcks.MAGMATOR.getAll());
    public static final Supplier<BlockEntityType<ThermoBlockEntity>> THERMO_GEN = register("thermo_gen", ThermoBlockEntity::new,
            () -> Blcks.THERMO_GENERATOR.getAll());
    public static final Supplier<BlockEntityType<ReactorBlockEntity>> REACTOR = register("reactor", ReactorBlockEntity::new,
            () -> Blcks.REACTOR.getAll());
    public static final Supplier<BlockEntityType<ReactorPartBlockEntity>> REACTOR_PART = register("reactor_part", ReactorPartBlockEntity::new,
            () -> Blcks.REACTOR.getAll());
    public static final Supplier<BlockEntityType<PlayerTransmitterBlockEntity>> PLAYER_TRANSMITTER = register("player_transmitter",
            PlayerTransmitterBlockEntity::new, () -> Blcks.PLAYER_TRANSMITTER.getAll());
    public static final Supplier<BlockEntityType<EnergyHopperBlockEntity>> ENERGY_HOPPER = register("energy_hopper", EnergyHopperBlockEntity::new,
            () -> Blcks.ENERGY_HOPPER.getAll());
    public static final Supplier<BlockEntityType<EnergyDischargerBlockEntity>> ENERGY_DISCHARGER = register("energy_discharger",
            EnergyDischargerBlockEntity::new,
            () -> Blcks.ENERGY_DISCHARGER.getAll());
}
