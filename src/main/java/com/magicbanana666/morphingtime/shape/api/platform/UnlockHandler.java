package com.magicbanana666.morphingtime.shape.api.platform;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/* TODO: Make unlocking server-side:
1. the player presses UNLOCK_KEY
2. a packet is sent to the server
3. the server checks with getTargetedLivingEntity whether the player is looking at a valid entity
4. a timer starts in the player's tick
5. every time the timer changes, the ray cast is checked again (only whether it is still the same entity, without blacklist checks)
6. the timer aborts if the targeted entity changes or is lost. If it runs out, the entity is unlocked and synced
*/
@SuppressWarnings("unused")
public class UnlockHandler {
    private static @Nullable LivingEntity getTargetedLivingEntity(@NotNull ServerPlayer player, double maxDistance) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance);

        // check for blocks in the ray cast's way
        HitResult blockHit = player.level().clip(new ClipContext(eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        endPos = blockHit.getLocation();

        // perform entity ray cast
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(maxDistance)).inflate(1.0);
        Predicate<Entity> entityFilter = entity -> (entity instanceof LivingEntity) && !entity.isSpectator() && entity.isAlive();

        EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(player, eyePos, endPos, searchBox, entityFilter, maxDistance);

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity livingEntity) {
            return livingEntity;
        } else {
            return null;
        }
    }
}