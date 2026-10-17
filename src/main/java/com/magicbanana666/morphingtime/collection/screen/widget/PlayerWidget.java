package com.magicbanana666.morphingtime.collection.screen.widget;

import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.compatibility.SkinAccess;
import com.magicbanana666.morphingtime.collection.mixin.client.accessor.ClientPlayerAccessor;
import com.magicbanana666.morphingtime.collection.network.NetworkHandler;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.network.impl.SwapPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;


public class PlayerWidget extends AbstractButton {
    private final Screen parent;
    private final boolean willCache = true;
    private final Identifier skinId;

    public PlayerWidget(int x, int y, int width, int height, Screen parent) {
        super(x, y, width, height, Component.nullToEmpty("Head"));
        this.parent = parent;
        ClientPlayerAccessor accessor = ((ClientPlayerAccessor) Minecraft.getInstance().player);
        if (accessor != null) {
            this.skinId = accessor.callGetPlayerInfo().getSkin().body().texturePath();
        } else {
            this.skinId = null;
        }
        setTooltip(Tooltip.create(Component.translatable("remorphed.player_icon")));
    }

    @Override
    public void extractContents(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        if (skinId != null) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, skinId, getX(), getY(), 8.0f, 8, getWidth(), getHeight(), 8, 8, 64, 64);
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, skinId, getX(), getY(), 40.0f, 8, getWidth(), getHeight(), 8, 8, 64, 64);
        }

        // Highlight when focused
        if (isHoveredOrFocused()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, MorphCollection.id("textures/gui/head_focus.png"), getX(), getY(), 0, 0, getWidth(), getHeight(), 16, 16, 16, 16);
        }
    }

    @Override
    public void onPress(@NotNull InputWithModifiers input) {
        if (Minecraft.getInstance().player != null) {
            if (PlayerShape.getCurrentShape(Minecraft.getInstance().player) != null) {
                SwapPackets.sendSwapRequest();
                parent.onClose();
            }
            if (MorphCollection.skinsEnabled && SkinAccess.getCurrentSkin(Minecraft.getInstance().player) != Minecraft.getInstance().player.getUUID()) {
                NetworkHandler.sendResetSkinPacket();
                parent.onClose();
            }
        }
    }

    @Override
    public void updateWidgetNarration(@NotNull NarrationElementOutput builder) {

    }
}
