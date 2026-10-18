package com.magicbanana666.morphingtime.collection.screen.widget;

import com.mojang.authlib.GameProfile;
import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.network.NetworkHandler;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.network.impl.SwapPackets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.Model;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import org.jetbrains.annotations.NotNull;


public class SkinWidget extends ShapeWidget {
    private final GameProfile skinProfile;
    private final int size;
    private final PlayerSkin playerSkin;
    private final Model.Simple model;

    public SkinWidget(int x, int y, int width, int height, @NotNull GameProfile skinProfile, PlayerSkin playerSkin, Model.Simple model, Screen parent, boolean isFavorite, boolean isCurrent, int availability) {
        super(x, y, width, height, parent, isFavorite, isCurrent, availability);
        this.size = (int) (MorphCollection.CONFIG.entity_size * (1 / (Math.max(EntityTypes.PLAYER.getHeight(), EntityTypes.PLAYER.getWidth()))));
        this.skinProfile = skinProfile;
        this.playerSkin = playerSkin;
        this.model = model;
    }

    @Override
    protected void sendFavoriteRequest(boolean isFavorite) {
        NetworkHandler.sendFavoriteRequest(skinProfile, isFavorite);
    }

    @Override
    protected void sendSwap2ndShapeRequest() {
        NetworkHandler.sendSwapSkinRequest(skinProfile);
        Player player = Minecraft.getInstance().player;
        if (player != null && PlayerShape.getCurrentShape(player) != null) {
            SwapPackets.sendSwapRequest();
        }
    }

    @Override
    protected Component displayName() {
        return Component.literal(skinProfile.name());
    }

    @Override
    protected void extractShape(GuiGraphicsExtractor guiGraphics) {
        PlayerSkin skin = this.playerSkin;
        if (skin != null) {
            int leftPos = (int) (getX() + (float) this.getWidth() / 2);
            int topPos = getY() + (getHeight() - 31) / 2; // the offsets below were tuned for a 31 pixel high card
            int k = leftPos - 20;
            int l = topPos - 35;
            int m = leftPos + 20;
            int n = topPos + 25;
            guiGraphics.skin(model, skin.body().texturePath(), size, -5, 30, -1.0625F, k, l, m, n);
        }
    }

    @Override
    void sendDeleteShapePacket() {
        NetworkHandler.sendDeleteShapePacket(skinProfile.id());
    }
}
