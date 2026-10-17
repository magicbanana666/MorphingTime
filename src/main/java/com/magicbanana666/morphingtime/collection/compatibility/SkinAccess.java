package com.magicbanana666.morphingtime.collection.compatibility;

import com.magicbanana666.morphingtime.skin.PlayerSkins;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Single access point for the player skin (morph into players) functions.
 */
public final class SkinAccess {

    public static @Nullable UUID getCurrentSkin(Player player) {
        return PlayerSkins.getCurrentSkin(player);
    }

    public static void setSkin(ServerPlayer player, @Nullable UUID skinId) {
        PlayerSkins.setSkin(player, skinId);
    }

    public static void clearSkin(ServerPlayer player) {
        PlayerSkins.setSkin(player, null);
    }
}