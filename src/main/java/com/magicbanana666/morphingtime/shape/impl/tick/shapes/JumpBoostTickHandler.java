package com.magicbanana666.morphingtime.shape.impl.tick.shapes;

import com.magicbanana666.morphingtime.shape.api.ShapeTickHandler;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class JumpBoostTickHandler<T extends LivingEntity> implements ShapeTickHandler<T> {

    private final int level;

    public JumpBoostTickHandler(int level) {
        this.level = level;
    }

    @Override
    public void tick(Player player, LivingEntity entity) {
        if (!player.level().isClientSide()) {
            if (player.tickCount % 5 == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 20 * 2, level, true, false));
            }
        }
    }
}
