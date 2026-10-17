package com.magicbanana666.morphingtime.collection.impl;

import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import net.minecraft.world.entity.EntityType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CollectionPlayerDataProvider {

    // Kills keyed by EntityType — variants are never tracked separately
    Map<EntityType<?>, Integer> collection$getUnlockedShapes();
    void collection$addKill(EntityType<?> type);
    int collection$getKills(EntityType<?> type);

    Set<EntityType<?>> collection$getFavoriteShapes();

    Map<UUID, Integer> collection$getUnlockedSkins();
    void collection$addKill(UUID skinId);
    int collection$getKills(UUID skinId);
    Set<UUID> collection$getFavoriteSkins();

    int collection$getCounter(EntityType<?> type);
    int collection$getCounter(UUID skinId);

    void collection$handleSwap(EntityType<?> to);
    void collection$handleSwap(UUID skinId);

    Map<EntityType<?>, Integer> collection$getShapeCounter();
    Map<UUID, Integer> collection$getSkinCounter();

    Map<EntityType<?>, ShapeType<?>> collection$getDefaultVariants();
    void collection$setDefaultVariant(EntityType<?> entityType, ShapeType<?> shapeType);
}