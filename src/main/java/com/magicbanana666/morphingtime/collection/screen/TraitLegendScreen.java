package com.magicbanana666.morphingtime.collection.screen;

import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.shape.traits.TraitIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Explains every badge and marker that can appear on a morph card.
 */
public class TraitLegendScreen extends Screen {
    public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
    @Nullable
    private LegendList list;

    public TraitLegendScreen() {
        super(Component.translatable("remorphed.legend.title"));
    }

    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);
        this.list = this.layout.addToContents(new LegendList(this.width, this.layout.getContentHeight(), this.layout.getHeaderHeight()));
        this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose()).width(200).build());
        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    protected void repositionElements() {
        this.layout.arrangeElements();
        if (this.list != null) {
            this.list.setSize(this.width, this.layout.getContentHeight());
            this.list.setPosition(0, this.layout.getHeaderHeight());
            this.list.refreshScrollAmount();
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(new MorphMenu());
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractTransparentBackground(graphics);
    }

    /**
     * One line of the legend: either a section heading or an icon with its name and description.
     */
    private interface Row {
        int height(Font font, int width);

        void draw(GuiGraphicsExtractor graphics, Font font, int x, int y, int width);
    }

    private record Heading(Component text, int color) implements Row {
        @Override
        public int height(Font font, int width) {
            return 20;
        }

        @Override
        public void draw(GuiGraphicsExtractor graphics, Font font, int x, int y, int width) {
            graphics.text(font, text, x, y + 8, color, true);
            graphics.fill(x, y + 18, x + width, y + 19, (color & 0x00FFFFFF) | 0x60000000);
        }
    }

    private interface IconDrawer {
        void draw(GuiGraphicsExtractor graphics, Font font, int x, int y);
    }

    private record Entry(IconDrawer icon, Component name, int nameColor, Component description) implements Row {
        private static final int ICON = 24;
        private static final int TEXT_X = ICON + 8;

        static Entry of(TraitIcons.Icon icon) {
            return new Entry((graphics, font, x, y) -> icon.draw(graphics, x, y, ICON), icon.name(), icon.category().textColor, icon.description());
        }

        private List<FormattedCharSequence> lines(Font font, int width) {
            return font.split(description, width - TEXT_X);
        }

        @Override
        public int height(Font font, int width) {
            return Math.max(ICON, 11 + lines(font, width).size() * 10) + 6;
        }

        @Override
        public void draw(GuiGraphicsExtractor graphics, Font font, int x, int y, int width) {
            icon.draw(graphics, font, x, y + 3);
            graphics.text(font, name, x + TEXT_X, y + 3, nameColor, true);
            int lineY = y + 14;
            for (FormattedCharSequence line : lines(font, width)) {
                graphics.text(font, line, x + TEXT_X, lineY, MenuStyle.TEXT_DIM, false);
                lineY += 10;
            }
        }
    }

    private static List<Row> buildRows() {
        List<Row> rows = new ArrayList<>();

        // what the card itself shows
        rows.add(new Heading(Component.translatable("remorphed.legend.section.card"), MenuStyle.TEXT));
        rows.add(new Entry((graphics, font, x, y) -> {
            MenuStyle.card(graphics, x, y, 24, 24, MenuStyle.CARD, MenuStyle.BORDER);
            graphics.blit(RenderPipelines.GUI_TEXTURED, MenuStyle.STAR, x + 5, y + 5, 0, 0, 14, 14, MenuStyle.STAR_SIZE, MenuStyle.STAR_SIZE, MenuStyle.STAR_SIZE, MenuStyle.STAR_SIZE);
        }, Component.translatable("remorphed.legend.favorite"), MenuStyle.TEXT, Component.translatable("remorphed.legend.favorite.desc")));
        rows.add(new Entry((graphics, font, x, y) -> {
            MenuStyle.card(graphics, x, y, 24, 24, MenuStyle.CARD, MenuStyle.ACCENT);
            graphics.outline(x + 1, y + 1, 22, 22, MenuStyle.ACCENT_GLOW);
        }, Component.translatable("remorphed.legend.current"), MenuStyle.ACCENT, Component.translatable("remorphed.legend.current.desc")));
        rows.add(new Entry((graphics, font, x, y) -> {
            MenuStyle.card(graphics, x, y, 24, 24, MenuStyle.CARD, MenuStyle.BORDER);
            MenuStyle.pill(graphics, font, "3", x + 22, y + 2);
        }, Component.translatable("remorphed.legend.uses"), MenuStyle.TEXT, Component.translatable("remorphed.legend.uses.desc")));
        rows.add(new Entry((graphics, font, x, y) -> {
            MenuStyle.card(graphics, x, y, 24, 24, MenuStyle.CARD, MenuStyle.BORDER);
            graphics.fill(x + 1, y + 1, x + 23, y + 23, MenuStyle.USED_UP);
            graphics.centeredText(font, "0", x + 12, y + 8, MenuStyle.TEXT_BAD);
        }, Component.translatable("remorphed.legend.used_up"), MenuStyle.TEXT_BAD, Component.translatable("remorphed.legend.used_up.desc")));

        rows.add(new Heading(TraitIcons.Category.ABILITY.title(), TraitIcons.Category.ABILITY.textColor));
        rows.add(Entry.of(TraitIcons.legendAbility()));

        List<TraitIcons.Icon> icons = TraitIcons.legendEntries();
        for (TraitIcons.Category category : List.of(TraitIcons.Category.STRENGTH, TraitIcons.Category.WEAKNESS, TraitIcons.Category.NEUTRAL)) {
            rows.add(new Heading(category.title(), category.textColor));
            if (category == TraitIcons.Category.STRENGTH) rows.add(Entry.of(TraitIcons.legendEffect(false)));
            if (category == TraitIcons.Category.WEAKNESS) rows.add(Entry.of(TraitIcons.legendEffect(true)));
            for (TraitIcons.Icon icon : icons) {
                if (icon.category() == category) {
                    rows.add(Entry.of(icon));
                }
            }
        }
        return rows;
    }

    private static class LegendList extends AbstractScrollArea {
        private final List<Row> rows = buildRows();

        LegendList(int width, int height, int y) {
            super(0, y, width, height, Component.empty(), AbstractScrollArea.defaultSettings(SCROLLBAR_WIDTH));
        }

        private int rowWidth() {
            return Math.min(MorphCollection.CONFIG.row_width, getWidth() - 40);
        }

        private Font font() {
            return Minecraft.getInstance().font;
        }

        @Override
        protected int contentHeight() {
            int height = 16;
            for (Row row : rows) {
                height += row.height(font(), rowWidth());
            }
            return height;
        }

        @Override
        protected double scrollRate() {
            return 20;
        }

        @Override
        protected int scrollBarX() {
            return getX() + (getWidth() + rowWidth()) / 2 + 10;
        }

        @Override
        protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
            int w = rowWidth();
            int x = getX() + (getWidth() - w) / 2;
            MenuStyle.card(graphics, x - 8, getY() + 2, w + 16, getHeight() - 4, MenuStyle.PANEL, MenuStyle.PANEL_BORDER);

            graphics.enableScissor(getX(), getY() + 3, getRight(), getBottom() - 3);
            int y = getY() - (int) scrollAmount() + 6;
            for (Row row : rows) {
                int h = row.height(font(), w);
                if (y + h > getY() && y < getBottom()) {
                    row.draw(graphics, font(), x, y, w);
                }
                y += h;
            }
            graphics.disableScissor();
            extractScrollbar(graphics, mouseX, mouseY);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            output.add(NarratedElementType.TITLE, Component.translatable("remorphed.legend.title"));
        }

        @Override
        public void playDownSound(@NotNull SoundManager handler) {
        }
    }
}
