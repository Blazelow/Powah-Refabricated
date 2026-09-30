package owmii.powah.compat.jei;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;

public abstract class AbstractCategory<T> implements IRecipeCategory<T> {
    private final Component title;
    private final int width;
    private final int height;
    private final IDrawable icon;

    public AbstractCategory(IGuiHelper guiHelper, ItemLike iconItemLike, Component title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.icon = guiHelper.createDrawableItemLike(iconItemLike);
    }

    @Override
    public final Component getTitle() {
        return title;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public final IDrawable getIcon() {
        return icon;
    }
}
