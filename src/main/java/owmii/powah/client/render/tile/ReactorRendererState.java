package owmii.powah.client.render.tile;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import owmii.powah.block.Tier;
import owmii.powah.block.reactor.ReactorBlockEntity;
import owmii.powah.block.reactor.ReactorPartBlockEntity;

public class ReactorRendererState extends BlockEntityRenderState {
    public boolean built;
    public Tier tier = Tier.STARTER;
    public boolean running;
    public float lightPulse;
    public boolean hasFuel;

    public void update(ReactorBlockEntity blockEntity) {
        built = blockEntity.isBuilt();
        tier = blockEntity.getTier();
        running = blockEntity.isRunning();
        lightPulse = blockEntity.bright.subSized();
        hasFuel = !blockEntity.fuel.isEmpty();
    }

    public void update(ReactorPartBlockEntity blockEntity) {
        built = blockEntity.isBuilt();
        tier = blockEntity.getBlock().getTier();
    }
}
