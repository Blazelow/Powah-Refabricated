package owmii.powah.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BoundPlayer(UUID gameProfileId, String name) {

    public static final Codec<BoundPlayer> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            UUIDUtil.CODEC.fieldOf("gameProfileId").forGetter(BoundPlayer::gameProfileId),
            Codec.STRING.fieldOf("name").forGetter(BoundPlayer::name)).apply(builder, BoundPlayer::new));

    public static final StreamCodec<ByteBuf, BoundPlayer> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, BoundPlayer::gameProfileId,
            ByteBufCodecs.STRING_UTF8, BoundPlayer::name,
            BoundPlayer::new);

}
