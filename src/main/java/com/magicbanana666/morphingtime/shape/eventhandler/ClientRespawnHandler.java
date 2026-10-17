package com.magicbanana666.morphingtime.shape.eventhandler;

import com.magicbanana666.morphingtime.core.event.client.ClientPlayerEvents;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import net.minecraft.client.player.LocalPlayer;


public final class ClientRespawnHandler implements ClientPlayerEvents.ClientPlayerRespawn {
    @Override
    public void respawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
        ((PlayerDataProvider) newPlayer).shape$setCurrentShape(((PlayerDataProvider) oldPlayer).shape$getCurrentShape());
        ((PlayerDataProvider) newPlayer).shape$set2ndShape(((PlayerDataProvider) oldPlayer).shape$get2ndShape());
        ((PlayerDataProvider) newPlayer).shape$setAbilityCooldown(((PlayerDataProvider) oldPlayer).shape$getAbilityCooldown());
        ((PlayerDataProvider) newPlayer).shape$setRemainingHostilityTime(((PlayerDataProvider) oldPlayer).shape$getRemainingHostilityTime());

        newPlayer.refreshDimensions();
    }
}
