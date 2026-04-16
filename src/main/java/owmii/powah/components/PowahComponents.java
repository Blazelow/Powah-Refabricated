package owmii.powah.components;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.ApiStatus;
import owmii.powah.Powah;
import owmii.powah.api.wrench.WrenchMode;

public final class PowahComponents {

    public static final DataComponentType<Long> ENERGY_STORED = register("energy_stored", builder -> builder
            .persistent(Codec.LONG)
            .networkSynchronized(ByteBufCodecs.VAR_LONG));

    public static final DataComponentType<CustomData> STORED_BLOCK_ENTITY_STATE = register("stored_block_entity_state", builder -> builder
            .persistent(CustomData.CODEC));

    public static final DataComponentType<BlockPos> LINK_ORB_POS = register("link_orb_pos", builder -> builder
            .persistent(BlockPos.CODEC)
            .networkSynchronized(BlockPos.STREAM_CODEC));

    public static final DataComponentType<BlockPos> LINK_ROD_POS = register("link_rod_pos", builder -> builder
            .persistent(BlockPos.CODEC)
            .networkSynchronized(BlockPos.STREAM_CODEC));

    public static final DataComponentType<WrenchMode> WRENCH_MODE = register("wrench_mode", builder -> builder
            .persistent(WrenchMode.CODEC)
            .networkSynchronized(WrenchMode.STREAM_CODEC));

    public static final DataComponentType<BoundPlayer> BOUND_PLAYER = register("bound_player", builder -> builder
            .persistent(BoundPlayer.CODEC)
            .networkSynchronized(BoundPlayer.STREAM_CODEC));

    public static final DataComponentType<Boolean> CHARGING = register("charging", builder -> builder
            .persistent(Codec.BOOL)
            .networkSynchronized(ByteBufCodecs.BOOL));

    private PowahComponents() {
    }

    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        var builder = DataComponentType.<T>builder();
        customizer.accept(builder);
        var componentType = builder.build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
                ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, name), componentType);
        return componentType;
    }

    public static void init() {
        // Called to trigger class loading and static registration
    }
}
