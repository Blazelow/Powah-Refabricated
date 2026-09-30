package owmii.powah.lib.logistics.fluid;

import java.util.function.Predicate;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class FluidTank {
    public static final long DROPLETS_PER_MB = FluidConstants.BUCKET / 1000;

    public enum FluidAction {
        EXECUTE,
        SIMULATE;

        public boolean simulate() {
            return this == SIMULATE;
        }
    }

    private final Storage storage = new Storage();
    protected Predicate<FluidStack> validator;
    protected int capacity;

    public FluidTank(int capacity) {
        this(capacity, e -> true);
    }

    public FluidTank(int capacity, Predicate<FluidStack> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    public net.fabricmc.fabric.api.transfer.v1.storage.Storage<FluidVariant> getStorage() {
        return storage;
    }

    public FluidTank setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public FluidTank setValidator(Predicate<FluidStack> validator) {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isFluidValid(FluidStack stack) {
        return validator.test(stack);
    }

    public int getCapacity() {
        return capacity;
    }

    @NotNull
    public FluidStack getFluid() {
        if (isEmpty()) {
            return FluidStack.EMPTY;
        }
        return new FluidStack(storage.variant.getFluid(), getFluidAmount());
    }

    public int getFluidAmount() {
        return (int) (storage.amount / DROPLETS_PER_MB);
    }

    public void setFluid(FluidStack stack) {
        if (stack.isEmpty()) {
            storage.variant = FluidVariant.blank();
            storage.amount = 0;
        } else {
            storage.variant = stack.variant();
            storage.amount = stack.getAmount() * DROPLETS_PER_MB;
        }
    }

    public boolean isEmpty() {
        return storage.variant.isBlank() || storage.amount <= 0;
    }

    public int getSpace() {
        return Math.max(0, capacity - getFluidAmount());
    }

    public FluidTank readFromNBT(CompoundTag nbt) {
        setFluid(FluidStack.parse(nbt.getCompound("tank")));
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        FluidStack fluid = getFluid();
        if (!fluid.isEmpty()) {
            nbt.put("tank", fluid.save(new CompoundTag()));
        }
        return nbt;
    }

    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }
        try (Transaction tx = Transaction.openOuter()) {
            long inserted = storage.insert(resource.variant(), resource.getAmount() * DROPLETS_PER_MB, tx);
            if (!action.simulate()) {
                tx.commit();
            }
            return (int) (inserted / DROPLETS_PER_MB);
        }
    }

    @NotNull
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (isEmpty() || maxDrain <= 0) {
            return FluidStack.EMPTY;
        }
        FluidVariant current = storage.variant;
        try (Transaction tx = Transaction.openOuter()) {
            long extracted = storage.extract(current, maxDrain * DROPLETS_PER_MB, tx);
            if (!action.simulate()) {
                tx.commit();
            }
            return extracted <= 0 ? FluidStack.EMPTY : new FluidStack(current.getFluid(), (int) (extracted / DROPLETS_PER_MB));
        }
    }

    protected void onContentsChanged() {
    }

    private final class Storage extends SingleVariantStorage<FluidVariant> {
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
            return variant.getFluid() != Fluids.EMPTY && isFluidValid(new FluidStack(variant.getFluid(), 1));
        }

        @Override
        protected void onFinalCommit() {
            onContentsChanged();
        }
    }
}
