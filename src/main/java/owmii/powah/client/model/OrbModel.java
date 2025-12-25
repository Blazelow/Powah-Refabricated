package owmii.powah.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.Identifier;
import owmii.powah.Powah;
import owmii.powah.block.energizing.EnergizingOrbBlockEntity;
import owmii.powah.client.render.tile.EnergizingOrbRenderer;
import owmii.powah.lib.client.util.RenderTypes;

public class OrbModel extends Model<Object> {
    private static final String CUBE = "cube";

    public OrbModel(ModelPart root) {
        super(root, RenderTypes::entityBlendedNoDept);
    }

    public static LayerDefinition createDefinition() {
        var meshDefinition = new MeshDefinition();
        var root = meshDefinition.getRoot();
        root.addOrReplaceChild(CUBE, CubeListBuilder.create().mirror().addBox(-2.5F, -2.5F, -2.5F, 5, 5, 5), PartPose.ZERO);

        return LayerDefinition.create(meshDefinition, 20, 10);
    }

    public static final Identifier TEXTURE = Powah.id("textures/model/tile/energy_charge.png");
}
