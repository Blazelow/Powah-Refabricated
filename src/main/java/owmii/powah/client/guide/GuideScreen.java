package owmii.powah.client.guide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class GuideScreen extends Screen {
    private static final int NAV_WIDTH = 124;
    private static final int NAV_ROW = 20;
    private static final int SLOT = 18;
    private static final int RECIPE_WIDTH = SLOT * 3 + 6 + 16 + 6 + SLOT;
    private static final int RECIPE_HEIGHT = SLOT * 3;
    private static final int LINK_COLOR = 0x55C7FF;

    private final GuideLibrary library = GuideLibrary.load();
    private final Map<String, Item> items = new HashMap<>();
    private final List<NavEntry> nav = new ArrayList<>();
    private List<Element> elements = List.of();
    private String current = "index";
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int navScroll;
    private int contentScroll;
    private int contentHeight;
    private ItemStack hovered = ItemStack.EMPTY;

    public GuideScreen() {
        super(Component.translatable("item.powah.book"));
    }

    private record NavEntry(GuidePage page, int depth) {
    }

    private interface Element {
        int height();

        void render(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, boolean view);

        @Nullable
        default String click(int x, int y, double mouseX, double mouseY) {
            return null;
        }
    }

    @Override
    protected void init() {
        panelWidth = Math.min(width - 20, 620);
        panelHeight = height - 20;
        panelX = (width - panelWidth) / 2;
        panelY = 10;
        nav.clear();
        addNav(null, 0);
        select(library.page(current) != null ? current : firstPage());
    }

    private String firstPage() {
        return nav.isEmpty() ? current : nav.get(0).page().id();
    }

    private void addNav(@Nullable String parent, int depth) {
        for (GuidePage page : library.children(parent)) {
            nav.add(new NavEntry(page, depth));
            addNav(page.id(), depth + 1);
        }
    }

    private int navTop() {
        return panelY + 6;
    }

    private int navBottom() {
        return panelY + panelHeight - 6;
    }

    private int contentLeft() {
        return panelX + NAV_WIDTH + 14;
    }

    private int contentRight() {
        return panelX + panelWidth - 14;
    }

    private int contentWidth() {
        return contentRight() - contentLeft();
    }

    private int contentTop() {
        return panelY + 8;
    }

    private int contentBottom() {
        return panelY + panelHeight - 8;
    }

    private void select(String id) {
        GuidePage page = library.page(id);
        current = id;
        contentScroll = 0;
        if (page == null) {
            elements = List.of();
            contentHeight = 0;
            return;
        }
        List<Element> built = new ArrayList<>();
        int width = contentWidth();
        for (GuidePage.Block block : page.blocks()) {
            built.add(layout(block, width));
        }
        elements = built;
        int total = 0;
        for (Element element : built) {
            total += element.height();
        }
        contentHeight = total;
    }

    private Item item(String id) {
        return items.computeIfAbsent(id, key -> {
            Identifier location = Identifier.tryParse(key);
            return location == null ? Items.AIR : BuiltInRegistries.ITEM.getValue(location);
        });
    }

    private MutableComponent component(List<GuidePage.Run> runs) {
        MutableComponent result = Component.empty();
        for (GuidePage.Run run : runs) {
            if (run.text() != null) {
                result.append(Component.literal(run.text()));
            } else if (run.item() != null) {
                Item item = item(run.item());
                Component name = item == Items.AIR ? Component.literal(run.item()) : new ItemStack(item).getHoverName();
                result.append(name.copy().withStyle(Style.EMPTY.withColor(LINK_COLOR)));
            }
        }
        return result;
    }

    private Element layout(GuidePage.Block block, int width) {
        if (block instanceof GuidePage.Heading heading) {
            return new HeadingElement(component(heading.runs()), width);
        } else if (block instanceof GuidePage.Paragraph paragraph) {
            return new ParagraphElement(component(paragraph.runs()), width);
        } else if (block instanceof GuidePage.Image image) {
            return new ImageElement(image, width);
        } else if (block instanceof GuidePage.Table table) {
            return new TableElement(table, width);
        } else if (block instanceof GuidePage.Recipes recipes) {
            return new RecipesElement(recipes, width);
        }
        return new LinksElement((GuidePage.Links) block);
    }

    private void slot(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.fill(x, y, x + SLOT, y + SLOT, 0xFF373737);
        graphics.fill(x + 1, y + 1, x + SLOT - 1, y + SLOT - 1, 0xFF8B8B8B);
    }

    private void drawStack(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y, int mouseX, int mouseY, boolean view, boolean decorate) {
        if (stack.isEmpty()) {
            return;
        }
        graphics.item(stack, x, y);
        if (decorate) {
            graphics.itemDecorations(font, stack, x, y);
        }
        if (view && mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
            hovered = stack;
        }
    }

    private final class HeadingElement implements Element {
        private final Component text;

        private HeadingElement(Component text, int width) {
            this.text = text;
        }

        @Override
        public int height() {
            return 30;
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, boolean view) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(x, y + 2);
            graphics.pose().scale(2.0F, 2.0F);
            graphics.text(font, text, 0, 0, 0xFFFFFFFF, true);
            graphics.pose().popMatrix();
            graphics.fill(x, y + 22, x + contentWidth(), y + 23, 0xFF555566);
        }
    }

    private final class ParagraphElement implements Element {
        private final List<net.minecraft.util.FormattedCharSequence> lines;

        private ParagraphElement(Component text, int width) {
            this.lines = font.split(text, width);
        }

        @Override
        public int height() {
            return lines.size() * 10 + 8;
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, boolean view) {
            for (int i = 0; i < lines.size(); i++) {
                graphics.text(font, lines.get(i), x, y + i * 10, 0xFFDDDDDD, false);
            }
        }
    }

    private final class ImageElement implements Element {
        private final GuidePage.Image image;
        private final int drawWidth;
        private final int drawHeight;

        private ImageElement(GuidePage.Image image, int width) {
            this.image = image;
            double scale = Math.min(1.0, (double) width / image.width());
            this.drawWidth = (int) (image.width() * scale);
            this.drawHeight = (int) (image.height() * scale);
        }

        @Override
        public int height() {
            return drawHeight + 10;
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, boolean view) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, image.texture(), x, y, 0.0F, 0.0F, drawWidth, drawHeight, image.width(), image.height(), image.width(), image.height());
        }
    }

    private final class TableElement implements Element {
        private final GuidePage.Table table;
        private final int[] columns;

        private TableElement(GuidePage.Table table, int width) {
            this.table = table;
            int count = 0;
            for (var row : table.rows()) {
                count = Math.max(count, row.size());
            }
            int[] widths = new int[count];
            for (var row : table.rows()) {
                for (int i = 0; i < row.size(); i++) {
                    widths[i] = Math.max(widths[i], cellWidth(row.get(i)));
                }
            }
            int gap = 16;
            int total = 0;
            for (int w : widths) {
                total += w + gap;
            }
            if (total > width) {
                gap = 8;
            }
            this.columns = new int[count];
            for (int i = 0; i < count; i++) {
                columns[i] = widths[i] + gap;
            }
        }

        private int cellWidth(List<GuidePage.Run> cell) {
            if (isItemCell(cell)) {
                return 20 + font.width(component(cell));
            }
            return font.width(component(cell));
        }

        private boolean isItemCell(List<GuidePage.Run> cell) {
            return cell.size() == 1 && cell.get(0).item() != null && item(cell.get(0).item()) != Items.AIR;
        }

        @Override
        public int height() {
            return table.rows().size() * 20 + 8;
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, boolean view) {
            for (int r = 0; r < table.rows().size(); r++) {
                var row = table.rows().get(r);
                int rowY = y + r * 20;
                int cellX = x;
                for (int c = 0; c < row.size(); c++) {
                    var cell = row.get(c);
                    if (isItemCell(cell)) {
                        drawStack(graphics, new ItemStack(item(cell.get(0).item())), cellX, rowY + 2, mouseX, mouseY, view, false);
                        graphics.text(font, component(cell), cellX + 20, rowY + 6, r == 0 ? 0xFFFFD060 : 0xFFFFFFFF, false);
                    } else {
                        graphics.text(font, component(cell), cellX, rowY + 6, r == 0 ? 0xFFFFD060 : 0xFFFFFFFF, false);
                    }
                    cellX += columns[c];
                }
                if (r == 0) {
                    graphics.fill(x, rowY + 19, cellX, rowY + 20, 0xFF555566);
                }
            }
        }
    }

    private final class RecipesElement implements Element {
        private final GuidePage.Recipes recipes;
        private final int[] xs;
        private final int[] ys;
        private final int height;

        private RecipesElement(GuidePage.Recipes recipes, int width) {
            this.recipes = recipes;
            this.xs = new int[recipes.recipes().size()];
            this.ys = new int[recipes.recipes().size()];
            int x = 0;
            int y = 0;
            for (int i = 0; i < xs.length; i++) {
                if (x > 0 && x + RECIPE_WIDTH > width) {
                    x = 0;
                    y += RECIPE_HEIGHT + 12;
                }
                xs[i] = x;
                ys[i] = y;
                x += RECIPE_WIDTH + 14;
            }
            this.height = y + RECIPE_HEIGHT + 14;
        }

        @Override
        public int height() {
            return height;
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, boolean view) {
            for (int i = 0; i < recipes.recipes().size(); i++) {
                var recipe = recipes.recipes().get(i);
                int bx = x + xs[i];
                int by = y + ys[i];
                for (int slot = 0; slot < 9; slot++) {
                    int sx = bx + (slot % 3) * SLOT;
                    int sy = by + (slot / 3) * SLOT;
                    GuideScreen.this.slot(graphics, sx, sy);
                    var options = recipe.grid().get(slot);
                    if (!options.isEmpty()) {
                        String id = options.get((int) ((Util.getMillis() / 1000L) % options.size()));
                        drawStack(graphics, new ItemStack(item(id)), sx + 1, sy + 1, mouseX, mouseY, view, false);
                    }
                }
                int ax = bx + SLOT * 3 + 6;
                int ay = by + SLOT + 9;
                graphics.fill(ax, ay - 1, ax + 12, ay + 2, 0xFFAAAAAA);
                graphics.fill(ax + 12, ay - 3, ax + 13, ay + 4, 0xFFAAAAAA);
                graphics.fill(ax + 13, ay - 2, ax + 14, ay + 3, 0xFFAAAAAA);
                graphics.fill(ax + 14, ay - 1, ax + 15, ay + 2, 0xFFAAAAAA);
                int rx = bx + SLOT * 3 + 6 + 16 + 6;
                int ry = by + SLOT;
                GuideScreen.this.slot(graphics, rx, ry);
                drawStack(graphics, new ItemStack(item(recipe.result()), recipe.count()), rx + 1, ry + 1, mouseX, mouseY, view, true);
            }
        }
    }

    private final class LinksElement implements Element {
        private final List<GuidePage> targets = new ArrayList<>();

        private LinksElement(GuidePage.Links links) {
            for (String id : links.pages()) {
                GuidePage page = library.page(id);
                if (page != null) {
                    targets.add(page);
                }
            }
        }

        @Override
        public int height() {
            return targets.size() * 22 + 8;
        }

        @Override
        public void render(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY, boolean view) {
            for (int i = 0; i < targets.size(); i++) {
                GuidePage page = targets.get(i);
                int rowY = y + i * 22;
                boolean over = view && mouseX >= x && mouseX < x + contentWidth() && mouseY >= rowY && mouseY < rowY + 20;
                graphics.fill(x, rowY, x + contentWidth(), rowY + 20, over ? 0xFF33334A : 0xFF24242F);
                iconFor(graphics, page, x + 2, rowY + 2);
                graphics.text(font, page.title(), x + 24, rowY + 6, over ? 0xFFFFFFFF : 0xFF000000 | LINK_COLOR, false);
            }
        }

        @Override
        public String click(int x, int y, double mouseX, double mouseY) {
            for (int i = 0; i < targets.size(); i++) {
                int rowY = y + i * 22;
                if (mouseX >= x && mouseX < x + contentWidth() && mouseY >= rowY && mouseY < rowY + 20) {
                    return targets.get(i).id();
                }
            }
            return null;
        }
    }

    private void iconFor(GuiGraphicsExtractor graphics, GuidePage page, int x, int y) {
        if (page.icon() != null) {
            Item item = item(page.icon());
            if (item != Items.AIR) {
                graphics.item(new ItemStack(item), x, y);
            }
        }
    }

    private void border(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int color) {
        graphics.fill(x0, y0, x1, y0 + 1, color);
        graphics.fill(x0, y1 - 1, x1, y1, color);
        graphics.fill(x0, y0, x0 + 1, y1, color);
        graphics.fill(x1 - 1, y0, x1, y1, color);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        hovered = ItemStack.EMPTY;
        graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xF0101018);
        border(graphics, panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF666680);

        int navX = panelX + 6;
        int navRight = panelX + NAV_WIDTH;
        graphics.fill(navX, navTop(), navRight, navBottom(), 0xFF17171F);
        graphics.enableScissor(navX, navTop(), navRight, navBottom());
        for (int i = 0; i < nav.size(); i++) {
            int rowY = navTop() + 2 + i * NAV_ROW - navScroll;
            if (rowY + NAV_ROW < navTop() || rowY > navBottom()) {
                continue;
            }
            NavEntry entry = nav.get(i);
            boolean selected = entry.page().id().equals(current);
            boolean over = mouseX >= navX && mouseX < navRight && mouseY >= rowY && mouseY < rowY + NAV_ROW
                    && mouseY >= navTop() && mouseY < navBottom();
            if (selected || over) {
                graphics.fill(navX, rowY, navRight, rowY + NAV_ROW, selected ? 0xFF33334A : 0xFF25252F);
            }
            int indent = entry.depth() * 8;
            iconFor(graphics, entry.page(), navX + 3 + indent, rowY + 2);
            graphics.text(font, entry.page().title(), navX + 23 + indent, rowY + 6, selected ? 0xFFFFFFFF : 0xFFBBBBCC, false);
        }
        graphics.disableScissor();

        int left = contentLeft();
        int top = contentTop();
        int bottom = contentBottom();
        boolean inContent = mouseX >= left && mouseX < contentRight() && mouseY >= top && mouseY < bottom;
        graphics.enableScissor(left, top, contentRight() + 6, bottom);
        int y = top - contentScroll;
        for (Element element : elements) {
            if (y + element.height() >= top && y <= bottom) {
                element.render(graphics, left, y, mouseX, mouseY, inContent);
            }
            y += element.height();
        }
        if (library.isEmpty()) {
            graphics.text(font, "No guide pages found.", left, top, 0xFFFF8888, false);
        }
        graphics.disableScissor();

        int viewHeight = bottom - top;
        if (contentHeight > viewHeight) {
            int barHeight = Math.max(20, viewHeight * viewHeight / contentHeight);
            int barY = top + (viewHeight - barHeight) * contentScroll / (contentHeight - viewHeight);
            graphics.fill(contentRight() + 4, top, contentRight() + 8, bottom, 0xFF24242F);
            graphics.fill(contentRight() + 4, barY, contentRight() + 8, barY + barHeight, 0xFF7777AA);
        }

        if (!hovered.isEmpty()) {
            graphics.setTooltipForNextFrame(font, hovered, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int navX = panelX + 6;
        int navRight = panelX + NAV_WIDTH;
        if (mouseX >= navX && mouseX < navRight && mouseY >= navTop() && mouseY < navBottom()) {
            int index = (int) ((mouseY - navTop() - 2 + navScroll) / NAV_ROW);
            if (index >= 0 && index < nav.size()) {
                select(nav.get(index).page().id());
                return true;
            }
        }
        if (mouseX >= contentLeft() && mouseX < contentRight() && mouseY >= contentTop() && mouseY < contentBottom()) {
            int y = contentTop() - contentScroll;
            for (Element element : elements) {
                String target = element.click(contentLeft(), y, mouseX, mouseY);
                if (target != null && library.page(target) != null) {
                    select(target);
                    return true;
                }
                y += element.height();
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int delta = (int) (-scrollY * 18);
        if (mouseX < panelX + NAV_WIDTH) {
            int max = Math.max(0, nav.size() * NAV_ROW + 4 - (navBottom() - navTop()));
            navScroll = Math.max(0, Math.min(max, navScroll + delta));
        } else {
            int max = Math.max(0, contentHeight - (contentBottom() - contentTop()));
            contentScroll = Math.max(0, Math.min(max, contentScroll + delta));
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
