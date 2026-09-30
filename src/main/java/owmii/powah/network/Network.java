package owmii.powah.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import owmii.powah.network.packet.InteractWithTankPacket;
import owmii.powah.network.packet.NextEnergyConfigPacket;
import owmii.powah.network.packet.NextRedstoneModePacket;
import owmii.powah.network.packet.SetChannelPacket;
import owmii.powah.network.packet.SwitchGenModePacket;

public final class Network {
    public static void register() {
        registerServerbound(NextEnergyConfigPacket.TYPE, NextEnergyConfigPacket.STREAM_CODEC);
        registerServerbound(NextRedstoneModePacket.TYPE, NextRedstoneModePacket.STREAM_CODEC);
        registerServerbound(SetChannelPacket.TYPE, SetChannelPacket.STREAM_CODEC);
        registerServerbound(SwitchGenModePacket.TYPE, SwitchGenModePacket.STREAM_CODEC);
        registerServerbound(InteractWithTankPacket.TYPE, InteractWithTankPacket.STREAM_CODEC);
    }

    private static <T extends ServerboundPacket> void registerServerbound(CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.serverboundPlay().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (packet, context) -> packet.handleOnServer(context.player()));
    }

    public static void toServer(ServerboundPacket packet) {
        ClientPlayNetworking.send(packet);
    }
}
