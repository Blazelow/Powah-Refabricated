package owmii.powah.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import owmii.powah.Powah;
import owmii.powah.lib.block.PowahBaseBlockEntity;
import owmii.powah.network.ServerboundPacket;

public record NextRedstoneModePacket(BlockPos pos) implements ServerboundPacket {
    public static final Type<NextRedstoneModePacket> TYPE = new Type<>(Powah.id("next_redstone_mode"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NextRedstoneModePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, NextRedstoneModePacket::pos,
            NextRedstoneModePacket::new);

    @Override
    public Type<NextRedstoneModePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        BlockEntity tileEntity = player.level().getBlockEntity(pos);
        if (tileEntity instanceof PowahBaseBlockEntity<?, ?> ate) {
            ate.nextRedstoneMode();
            ate.sync();
        }
    }
}
