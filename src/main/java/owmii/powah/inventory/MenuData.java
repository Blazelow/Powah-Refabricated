package owmii.powah.inventory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MenuData(byte[] bytes) {
    public static final StreamCodec<ByteBuf, MenuData> CODEC = ByteBufCodecs.BYTE_ARRAY.map(MenuData::new, MenuData::bytes);

    public static MenuData of(FriendlyByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return new MenuData(bytes);
    }

    public FriendlyByteBuf toBuffer() {
        return new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes));
    }
}
