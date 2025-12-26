package owmii.powah.compat.jei;

import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import owmii.powah.Powah;
import owmii.powah.block.Blcks;
import owmii.powah.client.PowahClient;
import owmii.powah.compat.common.FluidCoolant;
import owmii.powah.compat.common.MagmatorFuel;
import owmii.powah.compat.common.PassiveHeatSource;
import owmii.powah.compat.common.SolidCoolant;
import owmii.powah.item.Itms;
import owmii.powah.lib.client.screen.container.PowahBaseContainerScreen;

@JeiPlugin
public class PowahJeiPlugin implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new JeiEnergizingCategory(helper),
                new JeiFluidCoolantCategory(helper),
                new JeiHeatSourceCategory(helper),
                new JeiMagmatorCategory(helper),
                new JeiReactorFuelCategory(helper),
                new JeiSolidCoolantCategory(helper));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(JeiEnergizingCategory.TYPE.get(), Blcks.ENERGIZING_ORB.get());
        Blcks.ENERGIZING_ROD.getAll().forEach(block -> registration.addCraftingStation(JeiEnergizingCategory.TYPE.get(), block));
        Blcks.MAGMATOR.getAll().forEach(block -> registration.addCraftingStation(JeiMagmatorCategory.TYPE, block));
        Blcks.THERMO_GENERATOR.getAll().forEach(block -> {
            registration.addCraftingStation(JeiHeatSourceCategory.TYPE, block);
            registration.addCraftingStation(JeiFluidCoolantCategory.TYPE, block);
        });
        Blcks.REACTOR.getAll().forEach(block -> {
            registration.addCraftingStation(JeiSolidCoolantCategory.TYPE, block);
            registration.addCraftingStation(JeiFluidCoolantCategory.TYPE, block);
            registration.addCraftingStation(JeiReactorFuelCategory.TYPE, block);
        });
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(JeiEnergizingCategory.TYPE.get(), PowahClient.ENERGIZING_RECIPES);
        registration.addRecipes(JeiMagmatorCategory.TYPE, MagmatorFuel.getAll());
        registration.addRecipes(JeiFluidCoolantCategory.TYPE, FluidCoolant.getAll());
        registration.addRecipes(JeiSolidCoolantCategory.TYPE, SolidCoolant.getAll());
        registration.addRecipes(JeiHeatSourceCategory.TYPE, PassiveHeatSource.getAll());
        registration.addRecipes(JeiReactorFuelCategory.TYPE, JeiReactorFuelCategory.createRecipes());

        if (Powah.config().general.player_aerial_pearl)
            registration.addIngredientInfo(Itms.PLAYER_AERIAL_PEARL.get(), Component.translatable("jei.powah.player_aerial_pearl"));
        if (Powah.config().general.dimensional_binding_card)
            registration.addIngredientInfo(Itms.BINDING_CARD_DIM.get(), Component.translatable("jei.powah.binding_card_dim"));
        if (Powah.config().general.lens_of_ender)
            registration.addIngredientInfo(Itms.LENS_OF_ENDER.get(), Component.translatable("jei.powah.lens_of_ender"));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(PowahBaseContainerScreen.class, new IGuiContainerHandler<PowahBaseContainerScreen<?>>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(PowahBaseContainerScreen<?> containerScreen) {
                return containerScreen.getExtraAreas();
            }
        });
    }

    @Override
    public Identifier getPluginUid() {
        return Identifier.fromNamespaceAndPath(Powah.MOD_ID, "main");
    }
}
