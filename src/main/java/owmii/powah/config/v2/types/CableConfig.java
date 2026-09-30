package owmii.powah.config.v2.types;

import owmii.powah.block.Tier;
import owmii.powah.config.v2.values.TieredEnergyValues;

public class CableConfig {
    public TieredEnergyValues transfer_rates;

    public CableConfig(TieredEnergyValues transfer_rates) {
        this.transfer_rates = transfer_rates;
    }

    public long getTransfer(Tier variant) {
        return transfer_rates.get(variant);
    }
}
