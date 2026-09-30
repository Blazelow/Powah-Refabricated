package owmii.powah.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import owmii.powah.Powah;
import owmii.powah.block.ender.AbstractEnderTile;
import owmii.powah.network.ServerboundPacket;

public record SetChannelPacket(BlockPos pos, int channel) implements ServerboundPacket {
    public static final Type<SetChannelPacket> TYPE = new Type<>(Powah.id("set_channel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetChannelPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SetChannelPacket::pos,
            ByteBufCodecs.INT, SetChannelPacket::channel,
            SetChannelPacket::new);

    @Override
    public Type<SetChannelPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.serverLevel().getBlockEntity(pos) instanceof AbstractEnderTile<?> cell) {
            cell.getChannel().set(channel);
            cell.sync();
        }
    }
}
