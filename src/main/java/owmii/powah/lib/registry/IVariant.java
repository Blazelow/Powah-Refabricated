package owmii.powah.lib.registry;

import java.util.Locale;
import net.minecraft.nbt.CompoundTag;

public interface IVariant<V extends Enum<V> & IVariant<V>> {
    V[] getVariants();

    default String getName() {
        return ((Enum<?>) this).name().toLowerCase(Locale.ENGLISH);
    }

    default boolean isEmpty() {
        return this instanceof IVariant.Single || getVariants().length == 0;
    }

    @SuppressWarnings("unchecked")
    static <T extends IVariant> T getEmpty() {
        return (T) Single.SINGLE;
    }

    int ordinal();

    enum Single implements IVariant<Single> {
        SINGLE;

        @Override
        public Single[] getVariants() {
            return new Single[0];
        }
    }
}
