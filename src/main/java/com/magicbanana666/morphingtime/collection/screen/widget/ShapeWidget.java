package com.magicbanana666.morphingtime.collection.screen.widget;

import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.screen.MenuStyle;
import com.magicbanana666.morphingtime.shape.network.impl.SwapPackets;
import com.magicbanana666.morphingtime.shape.traits.ShapeTrait;
import com.magicbanana666.morphingtime.shape.traits.TraitIcons;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public abstract class ShapeWidget extends AbstractButton {
    /**
     * A trait or ability badge shown along the bottom of the card. Traits from add-ons that we have no icon for
     * keep their own icon ({@code foreign}).
     */
    protected record Badge(@Nullable TraitIcons.Icon icon, @Nullable ShapeTrait<?> foreign) {
        void draw(GuiGraphicsExtractor graphics, int x, int y) {
            if (icon != null) {
                icon.draw(graphics, x, y, TraitIcons.SIZE);
            } else if (foreign != null) {
                TraitIcons.drawForeign(foreign, graphics, x, y, TraitIcons.SIZE);
            }
        }

        List<Component> tooltip() {
            if (icon != null) return icon.tooltip();
            return foreign != null ? List.of(TraitIcons.foreignName(foreign)) : List.of();
        }
    }

    protected static final int BADGE_GAP = 1;
    protected static final int MAX_BADGE_ROWS = 2;

    private final Screen parent;
    private boolean crashed = false;
    private boolean isFavorite;
    private final boolean isCurrent;
    private int availability;

    public ShapeWidget(float x, float y, float width, float height, Screen parent, boolean isFavorite, boolean isCurrent, int availability) {
        super((int) x, (int) y, (int) width, (int) height, Component.nullToEmpty("WOLF"));
        this.parent = parent;
        this.isFavorite = isFavorite;
        this.isCurrent = isCurrent;
        this.availability = availability;
    }

    protected abstract void sendFavoriteRequest(boolean isFavorite);

    protected abstract void sendSwap2ndShapeRequest();

    protected abstract void extractShape(GuiGraphicsExtractor guiGraphics);

    protected abstract Component displayName();

    protected List<Badge> badges() {
        return List.of();
    }

    protected void setCrashed() {
        this.crashed = true;
    }

    /**
     * @return height of the badge strip at the bottom of the card, so the shape can be drawn above it
     */
    protected int badgeStripHeight() {
        int count = MorphCollection.displayDataInMenu ? badges().size() : 0;
        if (count == 0) return 0;
        int rows = Math.min(MAX_BADGE_ROWS, (count + badgesPerRow() - 1) / badgesPerRow());
        return rows * (TraitIcons.SIZE + BADGE_GAP) + 2;
    }

    private int badgesPerRow() {
        return Math.max(1, (getWidth() - 4 + BADGE_GAP) / (TraitIcons.SIZE + BADGE_GAP));
    }

    private void deletePoint() {
        this.playDownSound(Minecraft.getInstance().getSoundManager());
        if (this.availability > 0) {
            this.availability--;
            sendDeleteShapePacket();
        } else {
            // reopen screen to avoid negative shape availability. Do so after half a second so the server can process the loss
            if (isCurrent) {
                SwapPackets.sendSwapRequest();
            }
        }
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        // Add to favorites
        if (active && visible && isHovered() && Minecraft.getInstance().player != null) {
            int button = event.button();
            if (button == 1) {
                isFavorite = !isFavorite;
                sendFavoriteRequest(isFavorite);
                this.playDownSound(Minecraft.getInstance().getSoundManager());
            } else if (button == 2 && availability != -1) {
                deletePoint();
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractContents(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        if (crashed) {
            return;
        }

        // the card, inset by one pixel so neighbouring cards get a gap
        int x = getX() + 1;
        int y = getY() + 1;
        int w = getWidth() - 2;
        int h = getHeight() - 2;
        boolean hovered = isHoveredOrFocused();
        int border = isCurrent ? MenuStyle.ACCENT : hovered ? MenuStyle.BORDER_HOVER : MenuStyle.BORDER;
        MenuStyle.card(guiGraphics, x, y, w, h, hovered ? MenuStyle.CARD_HOVER : MenuStyle.CARD, border);
        if (isCurrent) {
            guiGraphics.outline(x + 1, y + 1, w - 2, h - 2, MenuStyle.ACCENT_GLOW);
        }

        extractShape(guiGraphics);

        // no morphs left: grey the card out
        if (availability == 0) {
            guiGraphics.fill(x + 1, y + 1, x + w - 1, y + h - 1, MenuStyle.USED_UP);
            guiGraphics.centeredText(parent.getFont(), Component.translatable("remorphed.card.used_up"), x + w / 2, y + 3, MenuStyle.TEXT_BAD);
        } else if (MorphCollection.displayDataInMenu && availability > 0) {
            MenuStyle.pill(guiGraphics, parent.getFont(), String.valueOf(availability), x + w - 2, y + 2);
        }

        if (isFavorite) {
            MenuStyle.star(guiGraphics, x + 2, y + 2);
        }

        List<Component> tooltip = null;
        if (MorphCollection.displayDataInMenu) {
            tooltip = extractBadges(guiGraphics, x, y + h, mouseX, mouseY);
        }

        if (isHovered()) {
            if (tooltip == null) {
                tooltip = new ArrayList<>();
                tooltip.add(displayName());
                if (isCurrent) {
                    tooltip.add(Component.translatable("remorphed.card.current").withStyle(ChatFormatting.GREEN));
                }
                if (availability > 0) {
                    tooltip.add(Component.translatable("remorphed.card.uses_left", availability).withStyle(ChatFormatting.GRAY));
                }
                tooltip.add(Component.translatable("remorphed.card.hint").withStyle(ChatFormatting.DARK_GRAY));
            }
            guiGraphics.setComponentTooltipForNextFrame(parent.getFont(), tooltip, mouseX, mouseY);
        }
    }

    /**
     * Draws the badges bottom-up from the bottom left of the card.
     *
     * @return the tooltip of the hovered badge, if any
     */
    private @Nullable List<Component> extractBadges(GuiGraphicsExtractor guiGraphics, int left, int bottom, int mouseX, int mouseY) {
        List<Badge> badges = badges();
        int perRow = badgesPerRow();
        int slots = perRow * MAX_BADGE_ROWS;
        // when they don't all fit, the last slot says how many are hidden
        int shown = badges.size() > slots ? slots - 1 : badges.size();
        List<Component> tooltip = null;

        for (int i = 0; i <= shown && i < slots; i++) {
            int bx = left + 2 + (i % perRow) * (TraitIcons.SIZE + BADGE_GAP);
            int by = bottom - 2 - TraitIcons.SIZE - (i / perRow) * (TraitIcons.SIZE + BADGE_GAP);
            if (i == shown) {
                if (shown < badges.size()) {
                    guiGraphics.centeredText(parent.getFont(), "+" + (badges.size() - shown), bx + TraitIcons.SIZE / 2, by + 2, MenuStyle.TEXT_DIM);
                }
                break;
            }
            Badge badge = badges.get(i);
            badge.draw(guiGraphics, bx, by);

            if (isHovered() && mouseX >= bx && mouseX < bx + TraitIcons.SIZE && mouseY >= by && mouseY < by + TraitIcons.SIZE) {
                guiGraphics.outline(bx - 1, by - 1, TraitIcons.SIZE + 2, TraitIcons.SIZE + 2, MenuStyle.TEXT);
                tooltip = badge.tooltip();
            }
        }
        return tooltip;
    }

    @Override
    public void onPress(@NotNull InputWithModifiers input) {
        // switch to new shape
        if (!isCurrent) {
            // Update 2nd Shape
            sendSwap2ndShapeRequest();
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            // close active screen handler
            parent.onClose();
        }
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        if (this.active && this.visible && isHovered() && event.key() == GLFW.GLFW_KEY_X && availability != -1) {
            deletePoint();
            return true;
        }
        return super.keyPressed(event);
    }

    abstract void sendDeleteShapePacket();

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput builder) {

    }
}
