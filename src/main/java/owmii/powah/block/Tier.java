package owmii.powah.block;

import com.mojang.serialization.Codec;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;
import owmii.powah.lib.registry.IVariant;

public enum Tier implements IVariant<Tier>, StringRepresentable {
    STARTER(0xA7A7A7),
    BASIC(0xA3AB9F),
    HARDENED(0xBBA993),
    BLAZING(0xE4B040),
    NIOTIC(0x13EED2),
    SPIRITED(0xAFE241),
    NITRO(0xD7746C),
    CREATIVE(0x8D29AD);

    public static final Codec<Tier> CODEC = StringRepresentable.fromEnum(Tier::values);

    private final int color;

    private final String serializedName = name().toLowerCase(Locale.ROOT);

    Tier(int color) {
        this.color = color;
    }

    @Override
    public Tier[] getVariants() {
        return values();
    }

    public static Tier[] getNormalVariants() {
        return new Tier[] { STARTER, BASIC, HARDENED, BLAZING, NIOTIC, SPIRITED, NITRO };
    }

    public int getColor() {
        return color;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
