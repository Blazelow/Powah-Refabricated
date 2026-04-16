package owmii.powah.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import owmii.powah.network.packet.InteractWithTankPacket;
import owmii.powah.network.packet.NextEnergyConfigPacket;
import owmii.powah.network.packet.NextRedstoneModePacket;
import owmii.powah.network.packet.SetChannelPacket;
import owmii.powah.network.packet.SwitchGenModePacket;

public final class Network {

    /** Called during mod init (server side) to register all payload types and handlers. */
    public static void registerServer() {
        PayloadTypeRegistry.playC2S().register(NextEnergyConfigPacket.TYPE, NextEnergyConfigPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(NextRedstoneModePacket.TYPE, NextRedstoneModePacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SetChannelPacket.TYPE, SetChannelPacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(SwitchGenModePacket.TYPE, SwitchGenModePacket.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(InteractWithTankPacket.TYPE, InteractWithTankPacket.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(NextEnergyConfigPacket.TYPE,
                (payload, context) -> context.server().execute(() -> payload.handleOnServer((ServerPlayer) context.player())));
        ServerPlayNetworking.registerGlobalReceiver(NextRedstoneModePacket.TYPE,
                (payload, context) -> context.server().execute(() -> payload.handleOnServer((ServerPlayer) context.player())));
        ServerPlayNetworking.registerGlobalReceiver(SetChannelPacket.TYPE,
                (payload, context) -> context.server().execute(() -> payload.handleOnServer((ServerPlayer) context.player())));
        ServerPlayNetworking.registerGlobalReceiver(SwitchGenModePacket.TYPE,
                (payload, context) -> context.server().execute(() -> payload.handleOnServer((ServerPlayer) context.player())));
        ServerPlayNetworking.registerGlobalReceiver(InteractWithTankPacket.TYPE,
                (payload, context) -> context.server().execute(() -> payload.handleOnServer((ServerPlayer) context.player())));
    }

    /** Called during client mod init to register client-side payload types (needed even for C2S). */
    public static void registerClient() {
        // C2S packets only need registration on the payload registry, already done in registerServer.
        // If we add clientbound packets in the future, register their handlers here.
    }

    /** Send a serverbound packet from the client. */
    public static void toServer(ServerboundPacket packet) {
        ClientPlayNetworking.send(packet);
    }
}
