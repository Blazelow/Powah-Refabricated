package owmii.powah.lib.logistics.fluid;

import java.util.function.Predicate;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class Tank extends FluidStacksResourceHandler {
    private Predicate<FluidStack> validator;
    private Runnable changed = () -> {
    };

    public Tank(int capacity) {
        this(capacity, _ -> true);
    }

    public Tank(int capacity, Predicate<FluidStack> validator) {
        super(1, capacity);
        this.validator = validator;
    }

    @Override
    public boolean isValid(int index, FluidResource resource) {
        return super.isValid(index, resource)
                && validator.test(resource.toStack(1));
    }

    public int getCapacity() {
        return capacity;
    }

    public FluidStack getFluid() {
        return stacks.getFirst();
    }

    public int getFluidAmount() {
        return getAmountAsInt(0);
    }

    public boolean isEmpty() {
        return getFluid().isEmpty();
    }

    public void setValidator(Predicate<FluidStack> validator) {
        this.validator = validator;
    }

    public void setChange(Runnable changed) {
        this.changed = changed;
    }

    @Override
    protected void onContentsChanged(int index, FluidStack previousContents) {
        this.changed.run();
    }
}
