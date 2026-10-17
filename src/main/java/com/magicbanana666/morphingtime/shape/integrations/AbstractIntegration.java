package com.magicbanana666.morphingtime.shape.integrations;

import com.magicbanana666.morphingtime.shape.ability.AbilityRegistry;
import com.magicbanana666.morphingtime.shape.api.blacklist.EntityBlacklist;
import com.magicbanana666.morphingtime.shape.api.variant.TypeProviderRegistry;
import com.magicbanana666.morphingtime.shape.traits.TraitRegistry;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractIntegration {
    /**
     * Called to load the integration (only when the required mod is loaded)
     */
    public void initialize() {

    }

    /**
     * Called to add custom abilities via {@link AbilityRegistry AbilityRegistry}
     */
    public void registerAbilities() {

    }

    /**
     * Called to add custom traits via {@link TraitRegistry TraitRegistry}
     */
    public void registerTraits() {

    }

    /**
     * Called to add custom variants via {@link TypeProviderRegistry TypeProviderRegistry}
     */
    public void registerTypeProvider() {

    }

    /**
     * Called to blacklist entities via {@link EntityBlacklist EntityBlacklist}
     */
    public void registerEntityBlacklist() {

    }


    /**
     * Called to modify the way entities attack a morphed player
     *
     * @param mob    the mob that wants to attack the player
     * @param target the player that is morphed
     * @return whether the mob cannot attack the morphed player
     */
    public boolean mightAttackInnocent(Mob mob, Player target) {
        return true;
    }
}
