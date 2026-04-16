package owmii.powah.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.lib.item.ItemBase;
import owmii.powah.lib.registry.VarReg;

public class Itms {

    public static final PowahBookItem BOOK = register("book", new PowahBookItem(new Item.Properties().stacksTo(1)));
    public static final WrenchItem WRENCH = register("wrench", new WrenchItem(new Item.Properties().stacksTo(1)));
    public static final CapacitorItem CAPACITOR_BASIC_TINY = register("capacitor_basic_tiny", new CapacitorItem(new Item.Properties()));
    public static final CapacitorItem CAPACITOR_BASIC = register("capacitor_basic", new CapacitorItem(new Item.Properties()));
    public static final CapacitorItem CAPACITOR_BASIC_LARGE = register("capacitor_basic_large", new CapacitorItem(new Item.Properties()));
    public static final CapacitorItem CAPACITOR_HARDENED = register("capacitor_hardened", new CapacitorItem(new Item.Properties()));
    public static final CapacitorItem CAPACITOR_BLAZING = register("capacitor_blazing", new CapacitorItem(new Item.Properties()));
    public static final CapacitorItem CAPACITOR_NIOTIC = register("capacitor_niotic", new CapacitorItem(new Item.Properties()));
    public static final CapacitorItem CAPACITOR_SPIRITED = register("capacitor_spirited", new CapacitorItem(new Item.Properties()));
    public static final CapacitorItem CAPACITOR_NITRO = register("capacitor_nitro", new CapacitorItem(new Item.Properties()));
    public static final VarReg<Tier, Item> BATTERY = new VarReg<>(BuiltInRegistries.ITEM, Powah.MOD_ID, "battery",
            variant -> new BatteryItem(new Item.Properties().stacksTo(1), variant), Tier.getNormalVariants());
    public static final AerialPearlItem AERIAL_PEARL = register("aerial_pearl", new AerialPearlItem(new Item.Properties()));
    public static final AerialPearlItem PLAYER_AERIAL_PEARL = register("player_aerial_pearl", new AerialPearlItem(new Item.Properties()));
    public static final ItemBase BLANK_CARD = register("blank_card", new ItemBase(new Item.Properties()));
    public static final BindingCardItem BINDING_CARD = register("binding_card", new BindingCardItem(new Item.Properties().stacksTo(1), false));
    public static final BindingCardItem BINDING_CARD_DIM = register("binding_card_dim",
            new BindingCardItem(new Item.Properties().stacksTo(1), true));
    public static final LensOfEnderItem LENS_OF_ENDER = register("lens_of_ender", new LensOfEnderItem(new Item.Properties()));
    public static final PhotoelectricPaneItem PHOTOELECTRIC_PANE = register("photoelectric_pane",
            new PhotoelectricPaneItem(new Item.Properties()));
    public static final ItemBase THERMOELECTRIC_PLATE = register("thermoelectric_plate", new ItemBase(new Item.Properties()));
    public static final ItemBase DIELECTRIC_PASTE = register("dielectric_paste", new ItemBase(new Item.Properties()));
    public static final ItemBase DIELECTRIC_ROD = register("dielectric_rod", new ItemBase(new Item.Properties()));
    public static final ItemBase DIELECTRIC_ROD_HORIZONTAL = register("dielectric_rod_horizontal", new ItemBase(new Item.Properties()));
    public static final ItemBase DIELECTRIC_CASING = register("dielectric_casing", new ItemBase(new Item.Properties()));
    public static final ItemBase ENERGIZED_STEEL = register("steel_energized", new ItemBase(new Item.Properties()));
    public static final ItemBase BLAZING_CRYSTAL = register("crystal_blazing", new ItemBase(new Item.Properties()));
    public static final ItemBase NIOTIC_CRYSTAL = register("crystal_niotic", new ItemBase(new Item.Properties()));
    public static final ItemBase SPIRITED_CRYSTAL = register("crystal_spirited", new ItemBase(new Item.Properties()));
    public static final ItemBase NITRO_CRYSTAL = register("crystal_nitro", new ItemBase(new Item.Properties()));
    public static final ItemBase ENDER_CORE = register("ender_core", new ItemBase(new Item.Properties()));
    public static final ChargedSnowballItem CHARGED_SNOWBALL = register("charged_snowball",
            new ChargedSnowballItem(new Item.Properties().stacksTo(16)));
    public static final UraniniteItem URANINITE_RAW = register("uraninite_raw", new UraniniteItem(new Item.Properties()));
    public static final UraniniteItem URANINITE = register("uraninite", new UraniniteItem(new Item.Properties()));

    private static <T extends Item> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(Powah.MOD_ID, name), item);
    }

    public static void init() {
        // Called to trigger class loading and static registration
    }
}
