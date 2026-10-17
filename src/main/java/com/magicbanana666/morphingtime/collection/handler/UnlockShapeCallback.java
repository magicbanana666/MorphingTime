package com.magicbanana666.morphingtime.collection.handler;

import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.impl.PlayerMorph;
import com.magicbanana666.morphingtime.collection.permission.PermissionManager;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.events.ShapeEvents;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import com.magicbanana666.morphingtime.shape.network.impl.UnlockPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import org.jetbrains.annotations.NotNull;

public class UnlockShapeCallback implements ShapeEvents.UnlockShapeCallback {
    @Override
    public InteractionResult unlock(ServerPlayer player, ShapeType<?> type) {
        if (type != null) {
            EntityType<? extends @NotNull LivingEntity> eType = type.getEntityType();
            // Check if player has permission to unlock this entity type (only if permissions are enabled)
            if (MorphCollection.CONFIG.usePermissions && !PermissionManager.canMorphIntoType(player, eType)) {
                return InteractionResult.FAIL;
            }

            // Let friendly mobs be unlocked with the unlock key if enabled. Only the unlock key request counts,
            // because the server checked there that the player is really looking at a mob of this type
            if (!MorphCollection.CONFIG.lockTransform && MorphCollection.CONFIG.unlockFriendlyNormal && UnlockPackets.isHandlingUnlockKey()) {
                LivingEntity entityToBeUnlocked = type.create(player.level(), player);
                if (entityToBeUnlocked != null && !(entityToBeUnlocked instanceof Enemy) && !MorphCollection.canUseShape(player, eType)) {
                    PlayerMorph.getUnlockedShapes(player).put(eType, MorphCollection.getKillToUnlock(eType));
                }
            }
            // Otherwise only allow shapes that were unlocked through kills
            if (!MorphCollection.canUseShape(player, eType)) {
                if (!eType.equals(EntityType.WOLF) || !Shapes.hasSpecialShape(player.getUUID())) {
                    return InteractionResult.FAIL;
                }
            }
        }

        return InteractionResult.PASS;
    }
}
