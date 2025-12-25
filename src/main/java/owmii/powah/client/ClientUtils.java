package owmii.powah.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

public final class ClientUtils {
    private ClientUtils() {
    }

    public static TextureAtlasSprite getStillTexture(FluidStack fluidStack) {
        var renderProps = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        var spriteId = renderProps.getStillTexture(fluidStack);
        return Minecraft.getInstance().getAtlasManager().get(new Material(TextureAtlas.LOCATION_BLOCKS, spriteId));
    }

    public static int getFluidColor(FluidStack fluidStack) {
        var renderProps = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        return renderProps.getTintColor(fluidStack);
    }
}
