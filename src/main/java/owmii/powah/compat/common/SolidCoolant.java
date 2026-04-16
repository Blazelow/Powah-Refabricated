package owmii.powah.compat.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import owmii.powah.Powah;
import owmii.powah.api.SolidCoolantConfig;
import owmii.powah.data.PowahDataLoader;

public record SolidCoolant(ResourceLocation id, Item item, int amount, int temperature) {
    public static List<SolidCoolant> getAll() {
        List<SolidCoolant> result = new ArrayList<>();

        for (var item : BuiltInRegistries.ITEM) {
            var id = BuiltInRegistries.ITEM.getKey(item);
            var cfg = PowahDataLoader.getSolidCoolant(item);
            if (cfg == null) continue;
            int amount = cfg.amount();
            int coldness = cfg.temperature();
            var recipeId = Powah.id("coolants/solid/" + id.getNamespace() + "/" + id.getPath());
            result.add(new SolidCoolant(recipeId, item, amount, coldness));
        }

        result.sort(Comparator.comparingInt(SolidCoolant::temperature).reversed());

        return result;
    }

}
