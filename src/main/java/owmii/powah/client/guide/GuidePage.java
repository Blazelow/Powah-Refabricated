package owmii.powah.client.guide;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record GuidePage(String id, String title, @Nullable String parent, @Nullable String icon, int position, List<Block> blocks) {
    public sealed interface Block permits Heading, Paragraph, Image, Table, Recipes, Links {
    }

    public record Run(@Nullable String text, @Nullable String item) {
    }

    public record Heading(List<Run> runs) implements Block {
    }

    public record Paragraph(List<Run> runs) implements Block {
    }

    public record Image(ResourceLocation texture, int width, int height) implements Block {
    }

    public record Table(List<List<List<Run>>> rows) implements Block {
    }

    public record Recipe(String result, int count, List<List<String>> grid) {
    }

    public record Recipes(List<Recipe> recipes) implements Block {
    }

    public record Links(List<String> pages) implements Block {
    }
}
