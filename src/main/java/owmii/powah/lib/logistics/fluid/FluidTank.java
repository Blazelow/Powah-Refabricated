package owmii.powah.lib.logistics.fluid;

import java.util.function.Predicate;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

/**
 * Single-slot fluid tank backed by FAPI SingleVariantStorage<FluidVariant>.
 *
 * NeoForge IFluidHandler has been replaced with FAPI's storage API.
 * Internal logic (fill/drain amounts) is the same; only the API surface changed.
 */
@SuppressWarnings("UnstableApiUsage")
public class FluidTank extends SingleVariantStorage<FluidVariant> {

    protected Predicate<FluidVariant> validator;
    protected long capacity;

    public FluidTank(int capacity) {
        this(capacity, v -> true);
    }

    public FluidTank(int capacity, Predicate<FluidVariant> validator) {
        this.capacity = capacity;
        this.validator = validator;
    }

    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return this.capacity;
    }

    @Override
    protected boolean canInsert(FluidVariant variant) {
        return validator.test(variant);
    }

    public FluidTank setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public FluidTank setValidator(Predicate<FluidVariant> validator) {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public boolean isFluidValid(@NotNull FluidVariant variant) {
        return validator.test(variant);
    }

    public int getCapacityBuckets() {
        return (int) Math.min(this.capacity, Integer.MAX_VALUE);
    }

    @NotNull
    public FluidVariant getFluid() {
        return this.variant;
    }

    /** Returns stored amount in millibuckets (1 bucket = 1000 mb on Fabric = 81000 droplets). */
    public int getFluidAmount() {
        // Convert from droplets (FAPI unit) to mB for API compatibility
        return (int) Math.min(this.amount / 81, Integer.MAX_VALUE);
    }

    /** Returns stored amount in FAPI droplets. */
    public long getFluidAmountDroplets() {
        return this.amount;
    }

    public boolean isEmpty() {
        return this.isResourceBlank();
    }

    public int getSpace() {
        return (int) Math.min((this.capacity - this.amount) / 81, Integer.MAX_VALUE);
    }

    public void setFluid(@NotNull FluidVariant variant, long amountDroplets) {
        this.variant = variant;
        this.amount = amountDroplets;
    }

    public FluidTank readFromNBT(CompoundTag nbt, HolderLookup.Provider registries) {
        if (nbt.contains("tank")) {
            var inner = nbt.getCompound("tank");
            this.variant = FluidVariant.fromNbt(inner.getCompound("variant"), registries);
            this.amount = inner.getLong("amount");
        } else {
            this.variant = FluidVariant.blank();
            this.amount = 0;
        }
        return this;
    }

    public CompoundTag writeToNBT(CompoundTag nbt, HolderLookup.Provider registries) {
        if (!this.isResourceBlank()) {
            var inner = new CompoundTag();
            inner.put("variant", this.variant.toNbt(registries));
            inner.putLong("amount", this.amount);
            nbt.put("tank", inner);
        }
        return nbt;
    }

    @Override
    protected void onFinalCommit() {
        onContentsChanged();
    }

    protected void onContentsChanged() {
    }

    /**
     * Returns this tank as a FAPI Storage<FluidVariant>.
     * SingleVariantStorage already implements Storage<FluidVariant>, so this returns `this`.
     */
    public net.fabricmc.fabric.api.transfer.v1.storage.Storage<FluidVariant> asFabricStorage() {
        return this;
    }

}