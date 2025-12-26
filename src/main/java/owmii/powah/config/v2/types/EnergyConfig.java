package owmii.powah.config.v2.types;

import owmii.powah.block.Tier;
import owmii.powah.config.v2.values.TieredEnergyValues;

public class EnergyConfig {
    public TieredEnergyValues capacities;
    public TieredEnergyValues transfer_rates;

    public EnergyConfig(TieredEnergyValues capacities, TieredEnergyValues transfer_rates) {
        this.capacities = capacities;
        this.transfer_rates = transfer_rates;
    }

    public long getCapacity(Tier variant) {
        return capacities.get(variant);
    }

    public long getTransfer(Tier variant) {
        return transfer_rates.get(variant);
    }
}
