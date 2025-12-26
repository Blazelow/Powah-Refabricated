package owmii.powah.config.v2.types;

import owmii.powah.block.Tier;
import owmii.powah.config.v2.values.TieredEnergyValues;

public class GeneratorConfig {
    public TieredEnergyValues capacities;
    public TieredEnergyValues transfer_rates;
    public TieredEnergyValues generation_rates;

    public GeneratorConfig(TieredEnergyValues capacities, TieredEnergyValues transfer_rates, TieredEnergyValues generation_rates) {
        this.capacities = capacities;
        this.transfer_rates = transfer_rates;
        this.generation_rates = generation_rates;
    }

    public long getCapacity(Tier variant) {
        return capacities.get(variant);
    }

    public long getTransfer(Tier variant) {
        return transfer_rates.get(variant);
    }

    public long getGeneration(Tier variant) {
        return generation_rates.get(variant);
    }
}
