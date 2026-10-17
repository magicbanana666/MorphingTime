package com.magicbanana666.morphingtime.shape.integrations.impl;

import com.magicbanana666.morphingtime.shape.integrations.AbstractIntegration;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class MobBattleModIntegration extends AbstractIntegration {
    public static final String MODID = "mobbattle";

    @Override
    public boolean mightAttackInnocent(Mob mob, Player player) {
        return mob.getTeam() == null || player.getTeam() == null;
    }
}
