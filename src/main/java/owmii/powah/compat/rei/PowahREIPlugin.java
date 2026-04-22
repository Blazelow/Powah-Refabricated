package owmii.powah.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import owmii.powah.Powah;
import owmii.powah.block.Blcks;
import owmii.powah.block.energizing.EnergizingRecipe;
import owmii.powah.compat.common.FluidCoolant;
import owmii.powah.compat.common.MagmatorFuel;
import owmii.powah.compat.common.PassiveHeatSource;
import owmii.powah.compat.common.SolidCoolant;
import owmii.powah.compat.rei.energizing.EnergizingCategory;
import owmii.powah.compat.rei.energizing.EnergizingDisplay;
import owmii.powah.compat.rei.magmator.MagmatorCategory;
import owmii.powah.compat.rei.magmator.MagmatorDisplay;
import owmii.powah.item.Itms;
import owmii.powah.lib.client.screen.container.AbstractContainerScreen;
import owmii.powah.data.PowahDataLoader;
import owmii.powah.recipe.Recipes;

public class PowahREIPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new MagmatorCategory());
        registry.add(new CoolantCategory());
        registry.add(new SolidCoolantCategory());
        registry.add(new HeatSourceCategory());
        registry.add(new EnergizingCategory());
        registry.add(new ReactorFuelCategory());

        registry.addWorkstations(EnergizingCategory.ID, EntryStacks.of(Blcks.ENERGIZING_ORB));
        Blcks.ENERGIZING_ROD.getAll().forEach(block -> registry.addWorkstations(EnergizingCategory.ID, EntryStacks.of(block)));
        Blcks.MAGMATOR.getAll().forEach(block -> registry.addWorkstations(MagmatorCategory.ID, EntryStacks.of(block)));
        Blcks.THERMO_GENERATOR.getAll().forEach(block -> {
            registry.addWorkstations(HeatSourceCategory.ID, EntryStacks.of(block));
            registry.addWorkstations(CoolantCategory.ID, EntryStacks.of(block));
        });
        Blcks.REACTOR.getAll().forEach(block -> {
            registry.addWorkstations(SolidCoolantCategory.ID, EntryStacks.of(block));
            registry.addWorkstations(CoolantCategory.ID, EntryStacks.of(block));
            registry.addWorkstations(ReactorFuelCategory.ID, EntryStacks.of(block));
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // Use registerFiller instead of registerRecipeFiller for compatibility with REI 16.0.799+
        registry.registerFiller(
            EnergizingRecipe.class,
            recipe -> recipe.value().getType() == Recipes.ENERGIZING,
            EnergizingDisplay::new
        );

        for (var item : BuiltInRegistries.ITEM) {
            var fuel = PowahDataLoader.getReactorFuel(item);
            if (fuel == null) continue;
            registry.add(new ReactorFuelDisplay(BuiltInRegistries.ITEM.getKey(item), fuel));
        }
        MagmatorFuel.getAll().forEach(recipe -> registry.add(new MagmatorDisplay(recipe)));
        FluidCoolant.getAll().forEach(recipe -> registry.add(new CoolantDisplay(recipe)));
        SolidCoolant.getAll().forEach(recipe -> registry.add(new SolidCoolantDisplay(recipe)));
        PassiveHeatSource.getAll().forEach(recipe -> registry.add(new PassiveHeatSourceDisplay(recipe)));

        if (Powah.config().general.player_aerial_pearl)
            BuiltinClientPlugin.getInstance().registerInformation(EntryStacks.of(Itms.PLAYER_AERIAL_PEARL), Component.empty(), l -> {
                l.add(Component.translatable("jei.powah.player_aerial_pearl"));
                return l;
            });
        if (Powah.config().general.dimensional_binding_card)
            BuiltinClientPlugin.getInstance().registerInformation(EntryStacks.of(Itms.BINDING_CARD_DIM), Component.empty(), l -> {
                l.add(Component.translatable("jei.powah.binding_card_dim"));
                return l;
            });
        if (Powah.config().general.lens_of_ender)
            BuiltinClientPlugin.getInstance().registerInformation(EntryStacks.of(Itms.LENS_OF_ENDER), Component.empty(), l -> {
                l.add(Component.translatable("jei.powah.lens_of_ender"));
                return l;
            });
    }

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(AbstractContainerScreen.class, new GuiContainerHandler());
    }
}
