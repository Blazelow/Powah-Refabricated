package owmii.powah.compat.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import owmii.powah.Powah;
import owmii.powah.api.FluidCoolantConfig;
import owmii.powah.data.PowahDataLoader;

public record FluidCoolant(ResourceLocation id, Fluid fluid, Set<BucketItem> buckets, int coldness) {
    public static List<FluidCoolant> getAll() {
        List<FluidCoolant> result = new ArrayList<>();

        for (var fluid : BuiltInRegistries.FLUID) {
            var id = BuiltInRegistries.FLUID.getKey(fluid);
            var coolantValue = PowahDataLoader.getFluidCoolant(fluid);
            if (coolantValue == null) continue;
            var heat = coolantValue;
            

            if (!fluid.isSource(fluid.defaultFluidState())) {
                continue;
            }

            var buckets = new HashSet<BucketItem>();
            var bucket = fluid.getBucket();
            if (bucket instanceof BucketItem bucketItem) {
                buckets.add(bucketItem);
            }

            result.add(new FluidCoolant(Powah.id("fluid_coolant/" + id.getNamespace() + "/" + id.getPath()), fluid, buckets, heat.temperature()));
        }

        // Order by coldness * amount
        result.sort(Comparator.comparingInt(p -> p.coldness));

        return result;
    }

}
