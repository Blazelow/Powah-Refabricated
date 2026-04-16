package owmii.powah.client;

import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public final class ClientUtils {
    private ClientUtils() {
    }

    @Nullable
    public static TextureAtlasSprite getStillTexture(FluidVariant fluidVariant) {
        var sprites = FluidVariantRendering.getSprites(fluidVariant);
        if (sprites == null || sprites.length == 0) return null;
        return sprites[0];
    }

    public static int getFluidColor(FluidVariant fluidVariant) {
        return FluidVariantRendering.getColor(fluidVariant);
    }
}
