package owmii.powah.lib.item;

public interface IEnergyContainingItem {
    Info getEnergyInfo();

    record Info(long capacity, long maxInsert, long maxExtract) {
    }
}
