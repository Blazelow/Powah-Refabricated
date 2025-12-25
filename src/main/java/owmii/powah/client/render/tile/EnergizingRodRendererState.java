package owmii.powah.client.render.tile;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import owmii.powah.block.Tier;

public class EnergizingRodRendererState extends BlockEntityRenderState {
    public @Nullable Vec3 orbCenter;
    public Tier tier = Tier.STARTER;
}
