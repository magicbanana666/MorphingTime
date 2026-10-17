package com.magicbanana666.morphingtime.collection.handler;

import com.magicbanana666.morphingtime.core.event.common.PlayerEvents;
import com.magicbanana666.morphingtime.collection.impl.PlayerMorph;
import com.magicbanana666.morphingtime.collection.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;

public class PlayerRespawnHandler implements PlayerEvents.PlayerRespawn {
    @Override
    public void clone(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        // Copy unlocked and favorite shapes
        PlayerMorph.getUnlockedShapes(newPlayer).clear();
        PlayerMorph.getUnlockedShapes(newPlayer).putAll(PlayerMorph.getUnlockedShapes(oldPlayer));
        PlayerMorph.getFavoriteShapes(newPlayer).clear();
        PlayerMorph.getFavoriteShapes(newPlayer).addAll(PlayerMorph.getFavoriteShapes(oldPlayer));
        // Copy unlocked and favorite player skins
        PlayerMorph.getUnlockedSkinIds(newPlayer).clear();
        PlayerMorph.getUnlockedSkinIds(newPlayer).putAll(PlayerMorph.getUnlockedSkinIds(oldPlayer));
        PlayerMorph.getFavoriteSkinIds(newPlayer).clear();
        PlayerMorph.getFavoriteSkinIds(newPlayer).addAll(PlayerMorph.getFavoriteSkinIds(oldPlayer));
        // Copy how often each shape and skin was used, and the picked variants
        PlayerMorph.getShapeCounter(newPlayer).clear();
        PlayerMorph.getShapeCounter(newPlayer).putAll(PlayerMorph.getShapeCounter(oldPlayer));
        PlayerMorph.getSkinCounter(newPlayer).clear();
        PlayerMorph.getSkinCounter(newPlayer).putAll(PlayerMorph.getSkinCounter(oldPlayer));
        PlayerMorph.getDefaultVariants(newPlayer).clear();
        PlayerMorph.getDefaultVariants(newPlayer).putAll(PlayerMorph.getDefaultVariants(oldPlayer));
        // Re-sync favorites to the client
        NetworkHandler.sendFavoriteSync(newPlayer);
    }
}
