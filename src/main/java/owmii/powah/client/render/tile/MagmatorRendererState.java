package owmii.powah.client.render.tile;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.neoforged.neoforge.fluids.FluidStack;

public class MagmatorRendererState extends BlockEntityRenderState {
    public FluidStack tank = FluidStack.EMPTY;
    public float fill;
}
