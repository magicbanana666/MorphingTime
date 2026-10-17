package com.magicbanana666.morphingtime.collection.screen;

import com.magicbanana666.morphingtime.shape.traits.TraitIcons;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Colours and small drawing helpers shared by the morph menu and the icon legend.
 */
public final class MenuStyle {
    public static final int PANEL = 0xB00B0D11;
    public static final int PANEL_BORDER = 0x40FFFFFF;
    public static final int CARD = 0xD0181B21;
    public static final int CARD_HOVER = 0xE0262A33;
    public static final int BORDER = 0xFF2E333D;
    public static final int BORDER_HOVER = 0xFF8A93A6;
    public static final int ACCENT = 0xFF5BD06B;
    public static final int ACCENT_GLOW = 0x505BD06B;
    public static final int USED_UP = 0xA0000000;
    public static final int TEXT = 0xFFF2F2F2;
    public static final int TEXT_DIM = 0xFFA4ABB8;
    public static final int TEXT_BAD = 0xFFF06A64;
    public static final int PILL = 0xFF2B3A55;

    public static final Identifier STAR = TraitIcons.texture("favorite_star");
    public static final int STAR_SIZE = 7;

    private MenuStyle() {
    }

    /**
     * A filled box with a one pixel border and cut corners.
     */
    public static void card(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int fill, int border) {
        graphics.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
        graphics.fill(x + 1, y, x + w - 1, y + 1, border);
        graphics.fill(x + 1, y + h - 1, x + w - 1, y + h, border);
        graphics.fill(x, y + 1, x + 1, y + h - 1, border);
        graphics.fill(x + w - 1, y + 1, x + w, y + h - 1, border);
    }

    /**
     * A small number badge, right-aligned to {@code right}.
     */
    public static void pill(GuiGraphicsExtractor graphics, Font font, String text, int right, int top) {
        int w = font.width(text) + 4;
        card(graphics, right - w, top, w, 11, PILL, 0xFF4A6290);
        graphics.text(font, text, right - w + 2, top + 2, TEXT, false);
    }

    public static void star(GuiGraphicsExtractor graphics, int x, int y) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, STAR, x, y, 0, 0, STAR_SIZE, STAR_SIZE, STAR_SIZE, STAR_SIZE, STAR_SIZE, STAR_SIZE);
    }
}
