package owmii.powah.lib.logistics.fluid;

import java.util.function.Predicate;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;

@SuppressWarnings("UnstableApiUsage")
public class Tank extends FluidTank {
    private Runnable changed = () -> {
    };

    public Tank(int capacity) {
        this(capacity, v -> true);
    }

    public Tank(int capacity, Predicate<FluidVariant> validator) {
        super(capacity, validator);
    }

    @Override
    public Tank setValidator(Predicate<FluidVariant> validator) {
        super.setValidator(validator);
        return this;
    }

    @Override
    public Tank setCapacity(int capacity) {
        super.setCapacity(capacity);
        return this;
    }

    public Tank setChange(Runnable changed) {
        this.changed = changed;
        return this;
    }

    private boolean sendUpdates = true;

    public void setSendUpdates(boolean sendUpdates) {
        this.sendUpdates = sendUpdates;
    }

    @Override
    public void onContentsChanged() {
        if (sendUpdates) {
            this.changed.run();
        }
    }
}
