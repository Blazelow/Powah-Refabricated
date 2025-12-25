package owmii.powah.client.render.tile;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import owmii.powah.block.Tier;
import owmii.powah.lib.logistics.Transfer;

public class CableRendererState extends BlockEntityRenderState {
    public final Transfer[] transfer = new Transfer[6];
    public Tier tier = Tier.STARTER;
}
