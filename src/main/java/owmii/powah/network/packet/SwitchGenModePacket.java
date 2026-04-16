package owmii.powah.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import owmii.powah.Powah;
import owmii.powah.block.reactor.ReactorTile;
import owmii.powah.network.ServerboundPacket;

public record SwitchGenModePacket(BlockPos pos) implements ServerboundPacket {
    public static final Type<SwitchGenModePacket> TYPE = new Type<>(Powah.id("switch_gen_mode"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SwitchGenModePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SwitchGenModePacket::pos,
            SwitchGenModePacket::new);

    @Override
    public Type<SwitchGenModePacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        var be = player.serverLevel().getBlockEntity(pos);
        if (be instanceof ReactorTile reactor) {
            reactor.setGenModeOn(!reactor.isGenModeOn());
        }
    }
}
