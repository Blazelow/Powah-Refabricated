package owmii.powah.lib.logistics.fluid;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public final class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);

    private final Fluid fluid;
    private int amount;

    public FluidStack(Fluid fluid, int amount) {
        this.fluid = fluid;
        this.amount = amount;
    }

    public Fluid getFluid() {
        return fluid;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void grow(int amount) {
        this.amount += amount;
    }

    public void shrink(int amount) {
        this.amount -= amount;
    }

    public boolean isEmpty() {
        return fluid == Fluids.EMPTY || amount <= 0;
    }

    public FluidStack copy() {
        return new FluidStack(fluid, amount);
    }

    public FluidVariant variant() {
        return FluidVariant.of(fluid);
    }

    public Component getHoverName() {
        return FluidVariantAttributes.getName(variant());
    }

    public static boolean isSameFluid(FluidStack a, FluidStack b) {
        return a.fluid == b.fluid;
    }

    public static FluidStack parse(CompoundTag tag) {
        if (!tag.contains("fluid")) {
            return EMPTY;
        }
        var id = ResourceLocation.tryParse(tag.getString("fluid"));
        if (id == null) {
            return EMPTY;
        }
        Fluid fluid = BuiltInRegistries.FLUID.get(id);
        return fluid == Fluids.EMPTY ? EMPTY : new FluidStack(fluid, tag.getInt("amount"));
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putString("fluid", BuiltInRegistries.FLUID.getKey(fluid).toString());
        tag.putInt("amount", amount);
        return tag;
    }
}
