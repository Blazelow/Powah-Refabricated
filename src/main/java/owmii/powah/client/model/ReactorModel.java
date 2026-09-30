package owmii.powah.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import owmii.powah.client.render.tile.ReactorRendererState;

public class ReactorModel extends Model<ReactorRendererState> {
    private static final String REACTOR = "reactor";

    public ReactorModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
    }

    public static LayerDefinition createDefinition() {
        var meshDefinition = new MeshDefinition();
        var root = meshDefinition.getRoot();
        root.addOrReplaceChild(REACTOR, CubeListBuilder.create().mirror().addBox(-24F, -32F, -24F, 48, 64, 48), PartPose.offset(0, -8F, 0F));

        return LayerDefinition.create(meshDefinition, 256, 128);
    }
}
