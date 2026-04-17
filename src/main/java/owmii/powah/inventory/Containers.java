package owmii.powah.inventory;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import owmii.powah.Powah;

public class Containers {

    public static final MenuType<EnergyCellContainer> ENERGY_CELL = register("energy_cell",
            EnergyCellContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<EnderCellContainer> ENDER_CELL = register("ender_cell",
            EnderCellContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<FurnatorContainer> FURNATOR = register("furnator",
            FurnatorContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<MagmatorContainer> MAGMATOR = register("magmator",
            MagmatorContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<PlayerTransmitterContainer> PLAYER_TRANSMITTER = register("player_transmitter",
            PlayerTransmitterContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<EnergyHopperContainer> ENERGY_HOPPER = register("energy_hopper",
            EnergyHopperContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<CableContainer> CABLE = register("cable",
            CableContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<ReactorContainer> REACTOR = register("reactor",
            ReactorContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<SolarContainer> SOLAR = register("solar",
            SolarContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<ThermoContainer> THERMO = register("thermo",
            ThermoContainer::new, BlockPos.STREAM_CODEC);
    public static final MenuType<DischargerContainer> DISCHARGER = register("discharger",
            DischargerContainer::new, BlockPos.STREAM_CODEC);

    private static <T extends AbstractContainerMenu, D> MenuType<T> register(
            String name,
            ExtendedScreenHandlerType.ExtendedFactory<T, D> factory,
            StreamCodec<? super RegistryFriendlyByteBuf, D> codec) {
        return Registry.register(
                BuiltInRegistries.MENU,
                ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, name),
                new ExtendedScreenHandlerType<>(factory, codec));
    }

    public static void init() {}
}
