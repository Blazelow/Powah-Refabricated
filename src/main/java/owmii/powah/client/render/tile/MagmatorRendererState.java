package owmii.powah.client.render.tile;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import owmii.powah.lib.logistics.fluid.FluidStack;

public class MagmatorRendererState extends BlockEntityRenderState {
    public FluidStack tank = FluidStack.EMPTY;
    public float fill;
}
