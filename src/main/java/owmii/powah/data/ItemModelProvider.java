package owmii.powah.data;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import owmii.powah.Powah;
import owmii.powah.item.Itms;

public class ItemModelProvider extends ModelSubProvider {
    public ItemModelProvider(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        super(blockModels, itemModels);
    }

    @Override
    protected void register() {
        for (Item item : Itms.BATTERY.getAll()) {
            flatSingleLayer(item, "item/");
        }
        flatSingleLayer(Itms.BOOK, "item/");
        flatSingleLayer(Itms.WRENCH, "item/");
        flatSingleLayer(Itms.CAPACITOR_BASIC_TINY, "item/");
        flatSingleLayer(Itms.CAPACITOR_BASIC, "item/");
        flatSingleLayer(Itms.CAPACITOR_BASIC_LARGE, "item/");
        flatSingleLayer(Itms.CAPACITOR_HARDENED, "item/");
        flatSingleLayer(Itms.CAPACITOR_BLAZING, "item/");
        flatSingleLayer(Itms.CAPACITOR_NIOTIC, "item/");
        flatSingleLayer(Itms.CAPACITOR_SPIRITED, "item/");
        flatSingleLayer(Itms.CAPACITOR_NITRO, "item/");
        flatSingleLayer(Itms.AERIAL_PEARL, "item/");
        flatSingleLayer(Itms.PLAYER_AERIAL_PEARL, "item/");
        flatSingleLayer(Itms.BLANK_CARD, "item/");
        flatSingleLayer(Itms.BINDING_CARD, "item/");
        flatSingleLayer(Itms.BINDING_CARD_DIM, "item/");
        flatSingleLayer(Itms.LENS_OF_ENDER, "item/");
        flatSingleLayer(Itms.PHOTOELECTRIC_PANE, "item/");
        flatSingleLayer(Itms.THERMOELECTRIC_PLATE, "item/");
        flatSingleLayer(Itms.DIELECTRIC_PASTE, "item/");
        flatSingleLayer(Itms.DIELECTRIC_ROD, "item/");
        flatSingleLayer(Itms.DIELECTRIC_ROD_HORIZONTAL, "item/");
        flatSingleLayer(Itms.DIELECTRIC_CASING, "item/");
        flatSingleLayer(Itms.ENERGIZED_STEEL, "item/");
        flatSingleLayer(Itms.BLAZING_CRYSTAL, "item/");
        flatSingleLayer(Itms.NIOTIC_CRYSTAL, "item/");
        flatSingleLayer(Itms.SPIRITED_CRYSTAL, "item/");
        flatSingleLayer(Itms.NITRO_CRYSTAL, "item/");
        flatSingleLayer(Itms.ENDER_CORE, "item/");
        flatSingleLayer(Itms.CHARGED_SNOWBALL, "item/");
        flatSingleLayer(Itms.URANINITE_RAW, "item/");
        flatSingleLayer(Itms.URANINITE, "item/");

    }

    private void flatSingleLayer(ItemLike item, String texture) {
        var model = ModelTemplates.FLAT_ITEM.create(item.asItem(), TextureMapping.layer0(Powah.id(texture)),
                itemModels.modelOutput);
        itemModels.itemModelOutput.accept(item.asItem(), ItemModelUtils.plainModel(model));
    }
}
