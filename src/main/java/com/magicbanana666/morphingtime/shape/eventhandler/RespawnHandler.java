package com.magicbanana666.morphingtime.shape.eventhandler;

import com.magicbanana666.morphingtime.core.event.common.PlayerEvents;
import com.magicbanana666.morphingtime.shape.api.PlayerShapeChanger;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import com.magicbanana666.morphingtime.shape.impl.ShapeStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;

import static com.magicbanana666.morphingtime.shape.Shapes.WAYPOINT_TRANSMIT_MODIFIER;

public final class RespawnHandler implements PlayerEvents.PlayerRespawn {
    @Override
    public void clone(ServerPlayer oldPlayer, @NotNull ServerPlayer newPlayer) {
        ((PlayerDataProvider) newPlayer).shape$setCurrentShape(((PlayerDataProvider) oldPlayer).shape$getCurrentShape());
        ((PlayerDataProvider) newPlayer).shape$set2ndShape(((PlayerDataProvider) oldPlayer).shape$get2ndShape());
        ((PlayerDataProvider) newPlayer).shape$setAbilityCooldown(((PlayerDataProvider) oldPlayer).shape$getAbilityCooldown());
        ((PlayerDataProvider) newPlayer).shape$setRemainingHostilityTime(((PlayerDataProvider) oldPlayer).shape$getRemainingHostilityTime());

        newPlayer.refreshDimensions();

        LivingEntity shape = ((PlayerDataProvider) newPlayer).shape$getCurrentShape();

        // hide morphed players from the locator bar if the config says so
        AttributeInstance waypointTransmitRange = newPlayer.getAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE);
        if (waypointTransmitRange != null) {
            if (shape != null && !Shapes.CONFIG.show_on_locator_bar) {
                waypointTransmitRange.addOrReplacePermanentModifier(WAYPOINT_TRANSMIT_MODIFIER);
            } else {
                waypointTransmitRange.removeModifier(WAYPOINT_TRANSMIT_MODIFIER);
            }
        }

        // The mob stats are temporary modifiers, so the new player doesn't have them yet
        ShapeStats.update(newPlayer, shape);

        PlayerShapeChanger.sync(newPlayer);
    }
}
