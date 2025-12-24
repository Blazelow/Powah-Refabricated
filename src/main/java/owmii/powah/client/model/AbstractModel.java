package owmii.powah.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import owmii.powah.lib.block.PowahAbstractBlockEntity;
import owmii.powah.lib.client.renderer.tile.AbstractTileRenderer;

public abstract class AbstractModel<T extends PowahAbstractBlockEntity<?, ?>, R extends AbstractTileRenderer<T>> extends Model {
    public AbstractModel(Function<Identifier, RenderType> function) {
        super(function);
    }

    public abstract void render(T te, R renderer, PoseStack matrix, MultiBufferSource rtb, int light, int ov);

    // We never render via normal entity rendering, we abuse these models to render via BlockEntityRenderer
    @Override
    public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, int p_350308_) {
    }
}
