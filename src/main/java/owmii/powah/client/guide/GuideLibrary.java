package owmii.powah.client.guide;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;
import owmii.powah.Powah;

public final class GuideLibrary {
    private final Map<String, GuidePage> pages = new LinkedHashMap<>();

    private GuideLibrary() {
    }

    public static GuideLibrary load() {
        var library = new GuideLibrary();
        var minecraft = Minecraft.getInstance();
        ResourceManager manager = minecraft.getResourceManager();
        String language = minecraft.getLanguageManager().getSelected();
        JsonObject index = readJson(manager, Powah.id("guide/index.json"));
        if (index == null) {
            return library;
        }
        for (JsonElement entry : index.getAsJsonArray("pages")) {
            String id = entry.getAsString();
            JsonObject json = readJson(manager, Powah.id("guide/" + language + "/" + id + ".json"));
            if (json == null) {
                json = readJson(manager, Powah.id("guide/en_us/" + id + ".json"));
            }
            if (json != null) {
                library.pages.put(id, parsePage(json));
            }
        }
        return library;
    }

    @Nullable
    public GuidePage page(String id) {
        return pages.get(id);
    }

    public boolean isEmpty() {
        return pages.isEmpty();
    }

    public List<GuidePage> roots() {
        return children(null);
    }

    public List<GuidePage> children(@Nullable String parent) {
        List<GuidePage> result = new ArrayList<>();
        for (GuidePage page : pages.values()) {
            boolean root = page.parent() == null || !pages.containsKey(page.parent());
            if (parent == null ? root : parent.equals(page.parent())) {
                result.add(page);
            }
        }
        result.sort(Comparator.comparingInt(GuidePage::position).thenComparing(GuidePage::title));
        return result;
    }

    @Nullable
    private static JsonObject readJson(ResourceManager manager, ResourceLocation location) {
        var resource = manager.getResource(location);
        if (resource.isEmpty()) {
            return null;
        }
        try (var reader = resource.get().openAsReader()) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException | RuntimeException e) {
            Powah.LOGGER.error("Failed to read guide resource {}", location, e);
            return null;
        }
    }

    private static GuidePage parsePage(JsonObject json) {
        List<GuidePage.Block> blocks = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray("blocks")) {
            GuidePage.Block block = parseBlock(element.getAsJsonObject());
            if (block != null) {
                blocks.add(block);
            }
        }
        return new GuidePage(
                json.get("id").getAsString(),
                json.get("title").getAsString(),
                optional(json, "parent"),
                optional(json, "icon"),
                json.get("position").getAsInt(),
                blocks);
    }

    @Nullable
    private static String optional(JsonObject json, String key) {
        JsonElement element = json.get(key);
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }

    @Nullable
    private static GuidePage.Block parseBlock(JsonObject json) {
        return switch (json.get("type").getAsString()) {
        case "heading" -> new GuidePage.Heading(runs(json.getAsJsonArray("runs")));
        case "paragraph" -> new GuidePage.Paragraph(runs(json.getAsJsonArray("runs")));
        case "image" -> new GuidePage.Image(ResourceLocation.parse(json.get("texture").getAsString()), json.get("w").getAsInt(),
                json.get("h").getAsInt());
        case "table" -> table(json.getAsJsonArray("rows"));
        case "recipes" -> recipes(json.getAsJsonArray("recipes"));
        case "links" -> {
            List<String> targets = new ArrayList<>();
            json.getAsJsonArray("pages").forEach(e -> targets.add(e.getAsString()));
            yield new GuidePage.Links(targets);
        }
        default -> null;
        };
    }

    private static List<GuidePage.Run> runs(JsonArray array) {
        List<GuidePage.Run> result = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject run = element.getAsJsonObject();
            result.add(new GuidePage.Run(optional(run, "t"), optional(run, "item")));
        }
        return result;
    }

    private static GuidePage.Table table(JsonArray rows) {
        List<List<List<GuidePage.Run>>> result = new ArrayList<>();
        for (JsonElement row : rows) {
            List<List<GuidePage.Run>> cells = new ArrayList<>();
            for (JsonElement cell : row.getAsJsonArray()) {
                cells.add(runs(cell.getAsJsonArray()));
            }
            result.add(cells);
        }
        return new GuidePage.Table(result);
    }

    private static GuidePage.Recipes recipes(JsonArray array) {
        List<GuidePage.Recipe> result = new ArrayList<>();
        for (JsonElement element : array) {
            JsonObject json = element.getAsJsonObject();
            List<List<String>> grid = new ArrayList<>();
            for (JsonElement slot : json.getAsJsonArray("grid")) {
                List<String> options = new ArrayList<>();
                if (!slot.isJsonNull()) {
                    slot.getAsJsonArray().forEach(o -> options.add(o.getAsString()));
                }
                grid.add(options);
            }
            result.add(new GuidePage.Recipe(json.get("result").getAsString(), json.get("count").getAsInt(), grid));
        }
        return new GuidePage.Recipes(result);
    }
}
