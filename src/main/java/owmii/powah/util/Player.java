package owmii.powah.util;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.util.FakePlayer;

public class Player {
    public static boolean isFake(net.minecraft.world.entity.player.Player player) {
        return player instanceof FakePlayer;
    }

    public static Optional<ServerPlayer> get(ServerLevel level, UUID uuid) {
        return Optional.ofNullable(level.getServer().getPlayerList().getPlayer(uuid));
    }

    public static Optional<ServerPlayer> get(ServerLevel level, String name) {
        return Optional.ofNullable(level.getServer().getPlayerList().getPlayerByName(name));
    }
}
