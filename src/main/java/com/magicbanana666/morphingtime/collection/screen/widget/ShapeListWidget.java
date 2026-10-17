package com.magicbanana666.morphingtime.collection.screen.widget;

import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.screen.MenuStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.KeyEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("UnusedReturnValue")

public class ShapeListWidget extends ContainerObjectSelectionList<ShapeListWidget.@NotNull ShapeRow> {
    private static final int ITEM_HEIGHT = 50;

    public ShapeListWidget(Minecraft minecraft, int width, @NotNull HeaderAndFooterLayout layout) {
        super(minecraft, width, layout.getContentHeight(), layout.getHeaderHeight(), ITEM_HEIGHT);
    }

    public int addRow(ShapeWidget[] widgets) {
        return addEntry(new ShapeRow(widgets));
    }

    public int rowHeight() {
        return defaultEntryHeight;
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }

    public static class ShapeRow extends ContainerObjectSelectionList.Entry<@NotNull ShapeRow> {
        private final ShapeWidget[] widgets;

        public ShapeRow(ShapeWidget[] widgets) {
            this.widgets = widgets;
        }

        @Override
        public void extractContent(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovering, float delta) {
            for (int i = 0; i < widgets.length; i++) {
                ShapeWidget widget = widgets[i];

                if (widget != null) {
                    int w = getContentWidth() / MorphCollection.CONFIG.shapes_per_row;

                    widget.setPosition(getContentX() + w * i, getContentY());
                    widget.setSize(w, getContentHeight());
                    widget.extractRenderState(guiGraphics, mouseX, mouseY, delta);
                }
            }
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(widgets);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(widgets);
        }

        @Override
        public boolean keyPressed(@NotNull KeyEvent event) {
            for (GuiEventListener child : children()) {
                if (child.keyPressed(event)) {
                    return true;
                }
            }
            return super.keyPressed(event);
        }
    }

    @Override
    public int getRowWidth() {
        return MorphCollection.CONFIG.row_width;
    }

    @Override
    protected void extractListBackground(@NotNull GuiGraphicsExtractor graphics) {
        // one dark panel behind the cards instead of the full-width vanilla list background
        MenuStyle.card(graphics, getRowLeft() - 6, getY() + 2, getRowWidth() + 12, getHeight() - 4, MenuStyle.PANEL, MenuStyle.PANEL_BORDER);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        for (ShapeRow child : children()) {
            if (child.keyPressed(event)) {
                return true;
            }
        }
        return super.keyPressed(event);
    }
}
