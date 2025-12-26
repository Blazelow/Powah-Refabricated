package owmii.powah.config.v2.types;

import owmii.powah.block.Tier;
import owmii.powah.config.v2.values.TieredEnergyValues;

public class ChargingConfig {
    public TieredEnergyValues capacities;
    public TieredEnergyValues transfer_rates;
    public TieredEnergyValues charging_rates;

    public ChargingConfig(TieredEnergyValues capacities, TieredEnergyValues transfer_rates, TieredEnergyValues charging_rates) {
        this.capacities = capacities;
        this.transfer_rates = transfer_rates;
        this.charging_rates = charging_rates;
    }

    public long getCapacity(Tier variant) {
        return capacities.get(variant);
    }

    public long getTransfer(Tier variant) {
        return transfer_rates.get(variant);
    }

    public long getChargingRate(Tier variant) {
        return charging_rates.get(variant);
    }
}
