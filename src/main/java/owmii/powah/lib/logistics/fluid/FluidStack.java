package owmii.powah.lib.logistics.fluid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public final class FluidStack {
    public static final FluidStack EMPTY = new FluidStack(Fluids.EMPTY, 0);
    public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").forGetter(FluidStack::getFluid),
            Codec.INT.fieldOf("amount").forGetter(FluidStack::getAmount)).apply(builder, FluidStack::new));

    private final Fluid fluid;
    private final int amount;

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

    public boolean isEmpty() {
        return fluid == Fluids.EMPTY || amount <= 0;
    }

    public FluidVariant variant() {
        return FluidVariant.of(fluid);
    }

    public Component getHoverName() {
        return FluidVariantAttributes.getName(variant());
    }
}
