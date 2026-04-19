package owmii.powah.lib.logistics.fluid;

import java.util.function.Predicate;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.NotNull;

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

    public int getFluidAmount() {
        return (int) Math.min(this.amount / 81, Integer.MAX_VALUE);
    }

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
            // Use codec with registry ops for 1.21.1 compatibility
            var ops = registries.createSerializationContext(NbtOps.INSTANCE);
            this.variant = FluidVariant.CODEC.parse(ops, inner.get("variant"))
                    .result().orElse(FluidVariant.blank());
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
            var ops = registries.createSerializationContext(NbtOps.INSTANCE);
            FluidVariant.CODEC.encodeStart(ops, this.variant)
                    .result().ifPresent(tag -> inner.put("variant", tag));
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

    public net.fabricmc.fabric.api.transfer.v1.storage.Storage<FluidVariant> asFabricStorage() {
        return this;
    }
}
