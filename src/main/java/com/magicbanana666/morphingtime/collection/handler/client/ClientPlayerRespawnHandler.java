package com.magicbanana666.morphingtime.collection.handler.client;

import com.magicbanana666.morphingtime.core.event.client.ClientPlayerEvents;
import com.magicbanana666.morphingtime.collection.impl.PlayerMorph;
import net.minecraft.client.player.LocalPlayer;

public class ClientPlayerRespawnHandler implements ClientPlayerEvents.ClientPlayerRespawn {
    @Override
    public void respawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
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
    }
}
