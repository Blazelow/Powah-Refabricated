package owmii.powah.config.v2.types;

import owmii.powah.block.Tier;
import owmii.powah.config.v2.values.TieredChannelValues;
import owmii.powah.config.v2.values.TieredEnergyValues;

public class EnderConfig {
    public TieredEnergyValues transfer_rates;
    public TieredChannelValues channels;

    public EnderConfig(TieredEnergyValues transfer_rates, TieredChannelValues channels) {
        this.transfer_rates = transfer_rates;
        this.channels = channels;
    }

    public long getTransfer(Tier variant) {
        return transfer_rates.get(variant);
    }

    public int getMaxChannels(Tier tier) {
        return channels.get(tier);
    }
}
