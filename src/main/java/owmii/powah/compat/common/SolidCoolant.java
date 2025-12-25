package owmii.powah.compat.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import owmii.powah.Powah;
import owmii.powah.api.SolidCoolantConfig;

public record SolidCoolant(Identifier id, Item item, int amount, int temperature) {
    public static List<SolidCoolant> getAll() {
        List<SolidCoolant> result = new ArrayList<>();

        for (var entry : BuiltInRegistries.ITEM.getDataMap(SolidCoolantConfig.DATA_MAP_TYPE).entrySet()) {
            var id = entry.getKey().identifier();
            int amount = entry.getValue().amount();
            int coldness = entry.getValue().temperature();

            var item = BuiltInRegistries.ITEM.getValue(id);
            var recipeId = Powah.id("coolants/solid/" + id.getNamespace() + "/" + id.getPath());
            result.add(new SolidCoolant(recipeId, item, amount, coldness));
        }

        result.sort(Comparator.comparingInt(SolidCoolant::temperature).reversed());

        return result;
    }

}
