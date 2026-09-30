package owmii.powah.api.wrench;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum WrenchMode implements StringRepresentable {
    CONFIG,
    LINK,
    ROTATE;

    public boolean config() {
        return this == CONFIG;
    }

    public boolean link() {
        return this == LINK;
    }

    public boolean rotate() {
        return this == ROTATE;
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static final Codec<WrenchMode> CODEC = StringRepresentable.fromValues(WrenchMode::values);
    public static final IntFunction<WrenchMode> BY_ID = ByIdMap.continuous(WrenchMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    public static final StreamCodec<ByteBuf, WrenchMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, WrenchMode::ordinal);
}
