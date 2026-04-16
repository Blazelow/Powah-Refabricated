package owmii.powah.item;

import java.util.stream.Collectors;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import owmii.powah.Powah;
import owmii.powah.block.Blcks;
import owmii.powah.block.Tier;
import owmii.powah.lib.item.ItemBlock;

public class CreativeTabs {
    public static final ResourceKey<CreativeModeTab> MAIN_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(),
            ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, "tab"));

    public static final CreativeModeTab MAIN = Registry.register(
            BuiltInRegistries.CREATIVE_MODE_TAB,
            MAIN_KEY.location(),
            FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.powah.tab"))
                    .icon(() -> new ItemStack(Blcks.ENERGY_CELL.get(Tier.BLAZING)))
                    .displayItems((params, output) -> {
                        output.accept(Itms.BOOK);
                        output.accept(Itms.WRENCH);
                        output.accept(Itms.CAPACITOR_BASIC_TINY);
                        output.accept(Itms.CAPACITOR_BASIC);
                        output.accept(Itms.CAPACITOR_BASIC_LARGE);
                        output.accept(Itms.CAPACITOR_HARDENED);
                        output.accept(Itms.CAPACITOR_BLAZING);
                        output.accept(Itms.CAPACITOR_NIOTIC);
                        output.accept(Itms.CAPACITOR_SPIRITED);
                        output.accept(Itms.CAPACITOR_NITRO);
                        output.acceptAll(Itms.BATTERY.getAll().stream().map(Item::getDefaultInstance).collect(Collectors.toList()));
                        output.accept(Itms.AERIAL_PEARL);
                        output.accept(Itms.PLAYER_AERIAL_PEARL);
                        output.accept(Itms.BLANK_CARD);
                        output.accept(Itms.BINDING_CARD);
                        output.accept(Itms.BINDING_CARD_DIM);
                        output.accept(Itms.LENS_OF_ENDER);
                        output.accept(Itms.PHOTOELECTRIC_PANE);
                        output.accept(Itms.THERMOELECTRIC_PLATE);
                        output.accept(Itms.DIELECTRIC_PASTE);
                        output.accept(Itms.DIELECTRIC_ROD);
                        output.accept(Itms.DIELECTRIC_ROD_HORIZONTAL);
                        output.accept(Itms.DIELECTRIC_CASING);
                        output.accept(Itms.ENERGIZED_STEEL);
                        output.accept(Itms.BLAZING_CRYSTAL);
                        output.accept(Itms.NIOTIC_CRYSTAL);
                        output.accept(Itms.SPIRITED_CRYSTAL);
                        output.accept(Itms.NITRO_CRYSTAL);
                        output.accept(Itms.ENDER_CORE);
                        output.accept(Itms.CHARGED_SNOWBALL);
                        output.accept(Itms.URANINITE_RAW);
                        output.accept(Itms.URANINITE);

                        for (var block : BuiltInRegistries.BLOCK) {
                            if (BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(Powah.MOD_ID)) {
                                if (block.asItem() instanceof ItemBlock<?> powahItem) {
                                    if (powahItem.getCreativeTab() == MAIN_KEY) {
                                        output.accept(powahItem);
                                    }
                                }
                            }
                        }
                    })
                    .build());

    public static void init() {
        // Called to trigger class loading and static registration
    }
}
