package owmii.powah.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import owmii.powah.Powah;
import owmii.powah.block.Tier;
import owmii.powah.lib.item.PowahBaseItem;
import owmii.powah.lib.registry.TieredItemReg;

public class Itms {
    public static final DeferredRegister.Items DR = DeferredRegister.createItems(Powah.MOD_ID);

    public static final DeferredItem<PowahBookItem> BOOK = DR.registerItem("book",
            props -> new PowahBookItem(props.stacksTo(1)));
    public static final DeferredItem<WrenchItem> WRENCH = DR.registerItem("wrench",
            props -> new WrenchItem(props.stacksTo(1)));
    public static final DeferredItem<CapacitorItem> CAPACITOR_BASIC_TINY = DR.registerItem("capacitor_basic_tiny",
            CapacitorItem::new);
    public static final DeferredItem<CapacitorItem> CAPACITOR_BASIC = DR.registerItem("capacitor_basic",
            CapacitorItem::new);
    public static final DeferredItem<CapacitorItem> CAPACITOR_BASIC_LARGE = DR.registerItem("capacitor_basic_large",
            CapacitorItem::new);
    public static final DeferredItem<CapacitorItem> CAPACITOR_HARDENED = DR.registerItem("capacitor_hardened",
            CapacitorItem::new);
    public static final DeferredItem<CapacitorItem> CAPACITOR_BLAZING = DR.registerItem("capacitor_blazing",
            CapacitorItem::new);
    public static final DeferredItem<CapacitorItem> CAPACITOR_NIOTIC = DR.registerItem("capacitor_niotic",
            CapacitorItem::new);
    public static final DeferredItem<CapacitorItem> CAPACITOR_SPIRITED = DR.registerItem("capacitor_spirited",
            CapacitorItem::new);
    public static final DeferredItem<CapacitorItem> CAPACITOR_NITRO = DR.registerItem("capacitor_nitro",
            CapacitorItem::new);
    public static final TieredItemReg BATTERY = new TieredItemReg(DR, "battery",
            (variant, props) -> new BatteryItem(props.stacksTo(1), variant), Tier.getNormalVariants());
    public static final DeferredItem<AerialPearlItem> AERIAL_PEARL = DR.registerItem("aerial_pearl",
            AerialPearlItem::new);
    public static final DeferredItem<AerialPearlItem> PLAYER_AERIAL_PEARL = DR.registerItem("player_aerial_pearl",
            AerialPearlItem::new);
    public static final DeferredItem<PowahBaseItem> BLANK_CARD = DR.registerItem("blank_card",
            PowahBaseItem::new);
    public static final DeferredItem<BindingCardItem> BINDING_CARD = DR.registerItem("binding_card",
            props -> new BindingCardItem(props.stacksTo(1), false));
    public static final DeferredItem<BindingCardItem> BINDING_CARD_DIM = DR.registerItem("binding_card_dim",
            props -> new BindingCardItem(props.stacksTo(1), true));
    public static final DeferredItem<LensOfEnderItem> LENS_OF_ENDER = DR.registerItem("lens_of_ender",
            LensOfEnderItem::new);
    public static final DeferredItem<PhotoelectricPaneItem> PHOTOELECTRIC_PANE = DR.registerItem("photoelectric_pane",
            PhotoelectricPaneItem::new);
    public static final DeferredItem<PowahBaseItem> THERMOELECTRIC_PLATE = DR.registerItem("thermoelectric_plate",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> DIELECTRIC_PASTE = DR.registerItem("dielectric_paste",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> DIELECTRIC_ROD = DR.registerItem("dielectric_rod",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> DIELECTRIC_ROD_HORIZONTAL = DR.registerItem("dielectric_rod_horizontal",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> DIELECTRIC_CASING = DR.registerItem("dielectric_casing",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> ENERGIZED_STEEL = DR.registerItem("steel_energized",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> BLAZING_CRYSTAL = DR.registerItem("crystal_blazing",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> NIOTIC_CRYSTAL = DR.registerItem("crystal_niotic",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> SPIRITED_CRYSTAL = DR.registerItem("crystal_spirited",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> NITRO_CRYSTAL = DR.registerItem("crystal_nitro",
            PowahBaseItem::new);
    public static final DeferredItem<PowahBaseItem> ENDER_CORE = DR.registerItem("ender_core",
            PowahBaseItem::new);
    public static final DeferredItem<ChargedSnowballItem> CHARGED_SNOWBALL = DR.registerItem("charged_snowball",
            props -> new ChargedSnowballItem(props.stacksTo(16)));
    public static final DeferredItem<UraniniteItem> URANINITE_RAW = DR.registerItem("uraninite_raw",
            UraniniteItem::new);
    public static final DeferredItem<UraniniteItem> URANINITE = DR.registerItem("uraninite",
            UraniniteItem::new);
}
