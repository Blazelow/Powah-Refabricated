package owmii.powah.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.FluidState;
import owmii.powah.lib.logistics.fluid.FluidStack;

public final class ClientUtils {
    private ClientUtils() {
    }

    private static FluidModel model(FluidStack fluidStack) {
        FluidState fluidState = fluidStack.getFluid().defaultFluidState();
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);
    }

    public static TextureAtlasSprite getStillTexture(FluidStack fluidStack) {
        return model(fluidStack).stillMaterial().sprite();
    }

    public static int getFluidColor(FluidStack fluidStack) {
        var tintSource = model(fluidStack).tintSource();
        if (tintSource == null) {
            return 0xFFFFFFFF;
        }
        var blockState = fluidStack.getFluid().defaultFluidState().createLegacyBlock();
        var minecraft = Minecraft.getInstance();
        if (minecraft.level != null && minecraft.player != null) {
            return tintSource.colorInWorld(blockState, minecraft.level, minecraft.player.blockPosition());
        }
        return tintSource.color(blockState);
    }
}
