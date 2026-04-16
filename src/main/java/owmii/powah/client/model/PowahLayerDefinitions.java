package owmii.powah.client.model;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import owmii.powah.Powah;

public class PowahLayerDefinitions {
    public static final ModelLayerLocation CABLE = new ModelLayerLocation(Powah.id("models"), "cable");
    public static final ModelLayerLocation ORB = new ModelLayerLocation(Powah.id("models"), "orb");
    public static final ModelLayerLocation REACTOR_PART = new ModelLayerLocation(Powah.id("models"), "reactor_part");
    public static final ModelLayerLocation REACTOR = new ModelLayerLocation(Powah.id("models"), "reactor");

    public static void register() {
        EntityModelLayerRegistry.registerModelLayer(CABLE, CableModel::createDefinition);
        EntityModelLayerRegistry.registerModelLayer(ORB, OrbModel::createDefinition);
        EntityModelLayerRegistry.registerModelLayer(REACTOR_PART, () -> CubeModel.createDefinition(16));
        EntityModelLayerRegistry.registerModelLayer(REACTOR, ReactorModel::createDefinition);
    }
}
