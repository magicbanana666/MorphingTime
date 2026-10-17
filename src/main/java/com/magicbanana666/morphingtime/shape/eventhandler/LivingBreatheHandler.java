package com.magicbanana666.morphingtime.shape.eventhandler;

import com.magicbanana666.morphingtime.core.event.common.EntityEvents;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.traits.TraitRegistry;
import com.magicbanana666.morphingtime.shape.traits.impl.AquaticTrait;
import com.magicbanana666.morphingtime.shape.traits.impl.UndrownableTrait;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class LivingBreatheHandler implements EntityEvents.LivingBreathe {
    @Override
    public boolean breathe(LivingEntity entity, boolean canBreathe) {
        if (entity instanceof Player) {
            LivingEntity shape = PlayerShape.getCurrentShape((Player) entity);

            if (shape != null) {
                boolean isInWater = entity.isInWater();

                for (AquaticTrait<LivingEntity> trait : TraitRegistry.get(shape, AquaticTrait.ID).stream().map(trait -> (AquaticTrait<LivingEntity>) trait).toList()) {
                    return isInWater ? trait.isAquatic : trait.isLand;
                }

                if (TraitRegistry.has(shape, UndrownableTrait.ID) || shape.canBreatheUnderwater() && isInWater) {
                    return true;
                }

            }
        }
        return canBreathe;
    }
}
