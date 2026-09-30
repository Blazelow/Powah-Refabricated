package owmii.powah.lib.logistics.fluid;

import java.util.function.Predicate;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Tank {
    public static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000;

    private final TankStorage storage = new TankStorage();
    private final int capacity;
    private Predicate<FluidStack> validator;
    private Runnable changed = () -> {
    };

    public Tank(int capacity) {
        this(capacity, _ -> true);
    }

    public Tank(int capacity, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    public Storage<FluidVariant> getStorage() {
        return storage;
    }

    public int getCapacity() {
        return capacity;
    }

    public FluidStack getFluid() {
        if (isEmpty()) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(storage.variant.getFluid(), getFluidAmount());
    }

    public int getFluidAmount() {
        return (int) (storage.amount / DROPLETS_PER_MB);
    }

    public boolean isEmpty() {
        return storage.variant.isBlank() || storage.amount <= 0;
    }

    public void setValidator(Predicate<FluidStack> validator) {
        this.validator = validator;
    }

    public void setChange(Runnable changed) {
        this.changed = changed;
    }

    public int drain(int maxAmount) {
        if (isEmpty() || maxAmount <= 0) {
            return 0;
        }
        try (Transaction tx = Transaction.openOuter()) {
            long extracted = storage.extract(storage.variant, maxAmount * DROPLETS_PER_MB, tx);
            tx.commit();
            return (int) (extracted / DROPLETS_PER_MB);
        }
    }

    public void serialize(ValueOutput output) {
        var fluid = getFluid();
        if (!fluid.isEmpty()) {
            output.store("fluid", FluidStack.CODEC, fluid);
        }
    }

    public void deserialize(ValueInput input) {
        var fluid = input.read("fluid", FluidStack.CODEC).orElse(FluidStack.EMPTY);
        if (fluid.isEmpty()) {
            storage.variant = FluidVariant.blank();
            storage.amount = 0;
        } else {
            storage.variant = fluid.variant();
            storage.amount = fluid.getAmount() * DROPLETS_PER_MB;
        }
    }

    private final class TankStorage extends SingleVariantStorage<FluidVariant> {
        @Override
        protected FluidVariant getBlankVariant() {
            return FluidVariant.blank();
        }

        @Override
        protected long getCapacity(FluidVariant variant) {
            return capacity * DROPLETS_PER_MB;
        }

        @Override
        protected boolean canInsert(FluidVariant variant) {
            return variant.getFluid() != Fluids.EMPTY && validator.test(new FluidStack(variant.getFluid(), 1));
        }

        @Override
        protected void onFinalCommit() {
            changed.run();
        }
    }
}
