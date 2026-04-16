package owmii.powah.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import owmii.powah.Powah;

public class Entities {

    public static final EntityType<ChargedSnowballEntity> CHARGED_SNOWBALL = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, "charged_snowball"),
            EntityType.Builder.<ChargedSnowballEntity>of(ChargedSnowballEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .updateInterval(10)
                    .clientTrackingRange(64)
                    .build("charged_snowball"));

    public static void init() {
        // Called to trigger class loading and static registration
    }
}
