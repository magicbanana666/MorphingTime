package com.magicbanana666.morphingtime.shape.impl.tick.shapes;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.ShapeTickHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class WardenTickHandler implements ShapeTickHandler<Warden> {

    @Override
    public void tick(Player player, Warden entity) {
        if (!player.level().isClientSide()) {
            if (player.tickCount % 20 == 0) {

                // Blind the player with the warden shape.
                if (Shapes.CONFIG.wardenIsBlinded) {
                    player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 3, 0, true, false));
                }

                // Blind other players near a player with the warden shape.
                if (Shapes.CONFIG.wardenBlindsNearby) {
                    for (Player target : ((ServerLevel) player.level()).getNearbyPlayers(TargetingConditions.DEFAULT, player,
                            new AABB(player.blockPosition()).inflate(16))) {
                        target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20 * 3, 0, true, false));
                    }
                }
            }
        }
    }
}
