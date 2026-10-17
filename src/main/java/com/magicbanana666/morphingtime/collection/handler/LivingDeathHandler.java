package com.magicbanana666.morphingtime.collection.handler;

import com.magicbanana666.morphingtime.UnlockEffectPayload;
import com.magicbanana666.morphingtime.core.event.common.EntityEvents;
import com.magicbanana666.morphingtime.collection.MorphCollection;
import com.magicbanana666.morphingtime.collection.impl.PlayerMorph;
import com.magicbanana666.morphingtime.collection.permission.PermissionManager;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.PlayerShapeChanger;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class LivingDeathHandler implements EntityEvents.LivingDeath {
    @SuppressWarnings("ConstantValue")
    @Override
    public InteractionResult die(LivingEntity entity, DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer killer) {
            if (!(entity instanceof Player)) {
                EntityType<?> type = entity.getType();
                ShapeType<?> shapeType = ShapeType.from(entity);
                if (type != null && (!Shapes.CONFIG.blacklistPreventsUnlocking || !Shapes.isPlayerBlacklisted(killer.getUUID()))) {
                    // Check if player has permission to unlock this entity type (only if permissions are enabled)
                    boolean canUnlock = !MorphCollection.CONFIG.usePermissions || PermissionManager.canMorphIntoType(killer, type);
                    if (canUnlock) {
                        int killsBefore = PlayerMorph.getKills(killer, type);
                        PlayerMorph.addKill(killer, type);
                        int killsNeeded = MorphCollection.getKillToUnlock(type);
                        if (killsBefore < killsNeeded && PlayerMorph.getKills(killer, type) >= killsNeeded) {
                            UnlockEffectPayload.send(killer, entity);
                        }

                        if (MorphCollection.CONFIG.autoTransform && PlayerMorph.getKills(killer, type) >= MorphCollection.getKillToUnlock(type)) {
                            if (PlayerShapeChanger.change2ndShape(killer, shapeType)) {
                                PlayerShape.updateShapes(killer, shapeType.create(killer.level(), killer));
                            }
                        }
                    }
                }
            } else if (entity instanceof Player) {
                // Check if the player may unlock player skins
                if (MorphCollection.skinsEnabled) {
                    // Any skin-related permission allows unlocking player skins.
                    // Permissions are only checked when the permission system is enabled.
                    boolean canUnlockPlayer = !MorphCollection.CONFIG.usePermissions ||
                            PermissionManager.hasPermission(killer, "command.addSkin") ||
                            PermissionManager.hasPermission(killer, "*");
                    if (canUnlockPlayer) {
                        addPlayerKill(killer, entity);
                    }
                } else {
                    // Track the player kill without permission checks
                    addPlayerKill(killer, entity);
                }
            }
        }

        return InteractionResult.PASS;
    }

    /** Counts a player kill and shows the unlock animation when it unlocks that player's skin. */
    private static void addPlayerKill(ServerPlayer killer, LivingEntity killed) {
        int killsBefore = PlayerMorph.getPlayerKills(killer, killed.getUUID());
        PlayerMorph.addPlayerKill(killer, killed.getUUID());
        int killsNeeded = MorphCollection.CONFIG.killToUnlockPlayers;
        if (killsBefore < killsNeeded && PlayerMorph.getPlayerKills(killer, killed.getUUID()) >= killsNeeded) {
            UnlockEffectPayload.send(killer, killed);
        }
    }
}
