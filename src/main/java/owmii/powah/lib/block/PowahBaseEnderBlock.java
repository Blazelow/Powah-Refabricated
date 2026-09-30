package owmii.powah.lib.block;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import owmii.powah.block.Tier;
import owmii.powah.config.v2.types.EnderConfig;

public abstract class PowahBaseEnderBlock<B extends PowahBaseEnderBlock<B>> extends PowahBaseEnergyBlock<B> {
    private final IntSupplier maxChannelsSupplier;

    public PowahBaseEnderBlock(Properties properties, Tier tier, LongSupplier capacitySupplier, LongSupplier transferSupplier,
            IntSupplier maxChannelsSupplier) {
        super(properties, tier, capacitySupplier, transferSupplier);
        this.maxChannelsSupplier = maxChannelsSupplier;
    }

    public PowahBaseEnderBlock(Properties properties, Tier tier, EnderConfig config) {
        this(properties, tier, () -> 0L, () -> config.getTransfer(tier), () -> config.getMaxChannels(tier));
    }

    public final int getMaxChannels() {
        return maxChannelsSupplier.getAsInt();
    }
}
