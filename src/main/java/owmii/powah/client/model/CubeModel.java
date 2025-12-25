package owmii.powah.client.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public class CubeModel extends Model<Object> {
    private static final String CUBE = "cube";

    public CubeModel(Function<Identifier, RenderType> renderLayer, ModelPart root) {
        super(root, renderLayer);
    }

    public static LayerDefinition createDefinition(int pixels) {
        float offset = -(pixels / 2.0F);

        var meshDefinition = new MeshDefinition();
        var root = meshDefinition.getRoot();
        root.addOrReplaceChild(CUBE, CubeListBuilder.create().mirror().addBox(offset, offset, offset, pixels, pixels, pixels), PartPose.ZERO);

        return LayerDefinition.create(meshDefinition, pixels * 4, pixels * 2);
    }
}
