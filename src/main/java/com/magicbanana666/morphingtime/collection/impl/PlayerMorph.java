package com.magicbanana666.morphingtime.collection.impl;

import com.mojang.authlib.GameProfile;
import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerMorph {

    public static Map<EntityType<?>, Integer> getUnlockedShapes(Player player) {
        return provider(player).collection$getUnlockedShapes();
    }

    public static void addKill(Player player, EntityType<?> type) {
        provider(player).collection$addKill(type);
    }

    public static int getKills(Player player, EntityType<?> type) {
        return provider(player).collection$getKills(type);
    }

    public static Set<EntityType<?>> getFavoriteShapes(Player player) {
        return provider(player).collection$getFavoriteShapes();
    }

    public static Map<UUID, Integer> getUnlockedSkinIds(Player player) {
        return provider(player).collection$getUnlockedSkins();
    }

    public static void addPlayerKill(Player player, UUID uuid) {
        provider(player).collection$addKill(uuid);
    }

    public static int getPlayerKills(Player player, UUID uuid) {
        return provider(player).collection$getKills(uuid);
    }

    public static Set<GameProfile> getFavoriteSkins(Player player) {
        return getFavoriteSkinIds(player).stream()
                .map(id -> MorphCollection.getGameProfile(player, id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    public static Set<UUID> getFavoriteSkinIds(Player player) {
        return provider(player).collection$getFavoriteSkins();
    }

    public static void handleSwap(Player player, EntityType<?> type) {
        provider(player).collection$handleSwap(type);
    }

    public static void handleSwap(Player player, UUID skinId) {
        provider(player).collection$handleSwap(skinId);
    }

    public static int getCounter(Player player, EntityType<?> type) {
        return provider(player).collection$getCounter(type);
    }

    public static int getCounter(Player player, UUID skinId) {
        return provider(player).collection$getCounter(skinId);
    }

    public static Map<EntityType<?>, Integer> getShapeCounter(Player player) {
        return provider(player).collection$getShapeCounter();
    }

    public static Map<UUID, Integer> getSkinCounter(Player player) {
        return provider(player).collection$getSkinCounter();
    }

    private static CollectionPlayerDataProvider provider(Player player) {
        return (CollectionPlayerDataProvider) player;
    }

    public static Map<EntityType<?>, ShapeType<?>> getDefaultVariants(Player player) {
        return provider(player).collection$getDefaultVariants();
    }

    public static void setDefaultVariant(Player player, EntityType<?> entityType, ShapeType<?> shapeType) {
        provider(player).collection$setDefaultVariant(entityType, shapeType);
    }
}