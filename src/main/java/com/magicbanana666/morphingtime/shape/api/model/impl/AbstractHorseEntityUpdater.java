package com.magicbanana666.morphingtime.shape.api.model.impl;

import com.magicbanana666.morphingtime.shape.api.model.EntityUpdater;
import com.magicbanana666.morphingtime.shape.mixin.accessor.AbstractHorseAccessor;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;


public class AbstractHorseEntityUpdater<H extends AbstractHorse> implements EntityUpdater<H> {
    @Override
    public void update(Player player, H horse) {
        // block some animations
        ((AbstractHorseAccessor) horse).setEatAnim(0);
        ((AbstractHorseAccessor) horse).setEatAnimO(0);
        ((AbstractHorseAccessor) horse).setMouthAnim(0);
        ((AbstractHorseAccessor) horse).setMouthAnimO(0);

        // stand while jumping
        ((AbstractHorseAccessor) horse).setStandAnimO(((AbstractHorseAccessor) horse).getStandAnim());
        if (player.isJumping()) {
            horse.setStanding(200); // arbitrary value that keeps the rearing animation going while jumping
        }
        if (((AbstractHorseAccessor) horse).getStandCounter() <= 200) {
            if (player.onGround()) {
                ((AbstractHorseAccessor) horse).setStandAnim(Math.max(((0.8F * ((AbstractHorseAccessor) horse).getStandAnim() * ((AbstractHorseAccessor) horse).getStandAnim() * ((AbstractHorseAccessor) horse).getStandAnim() - ((AbstractHorseAccessor) horse).getStandAnim()) * 0.6F - 0.05F), 0));
                horse.setStanding(((AbstractHorseAccessor) horse).getStandCounter() - 1);
                if (((AbstractHorseAccessor) horse).getStandCounter() < 100) {
                    horse.clearStanding();
                }
            } else {
                ((AbstractHorseAccessor) horse).setStandAnim(Math.min(((AbstractHorseAccessor) horse).getStandAnim() + ((1.0F - ((AbstractHorseAccessor) horse).getStandAnim()) * 0.4F + 0.05F), 1));
            }
        }
    }
}
