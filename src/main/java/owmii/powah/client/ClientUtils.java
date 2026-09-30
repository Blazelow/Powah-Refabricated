package owmii.powah.client;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;
import owmii.powah.lib.logistics.fluid.FluidStack;

public final class ClientUtils {
    private ClientUtils() {
    }

    @Nullable
    public static TextureAtlasSprite getStillTexture(FluidStack fluidStack) {
        return FluidVariantRendering.getSprite(fluidStack.variant());
    }

    public static int getFluidColor(FluidStack fluidStack) {
        return FluidVariantRendering.getColor(fluidStack.variant());
    }
}
