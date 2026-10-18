package com.magicbanana666.morphingtime.core.fabric;

import com.magicbanana666.morphingtime.core.event.common.CommandEvents;
import com.magicbanana666.morphingtime.core.event.common.EntityEvents;
import com.magicbanana666.morphingtime.core.event.common.ServerLevelEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.ApiStatus;

/**
 * Fires the core events from the Fabric API callbacks. The events without a Fabric API callback
 * (sleeping, breathing, destroy speed) are fired by the mixins in {@code core.fabric.mixin}.
 * The reload listeners are registered in {@link com.magicbanana666.morphingtime.core.registration.fabric.SynchronizedReloadListenerRegistryImpl}.
 */
@SuppressWarnings("unused")
@ApiStatus.Internal
public final class CoreFabricEventHandler {
    private CoreFabricEventHandler() {
    }

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) ->
                EntityEvents.LIVING_DEATH.invoke().die(entity, source) != InteractionResult.FAIL);

        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) ->
                CommandEvents.REGISTRATION.invoke().register(dispatcher, buildContext, selection));

        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents.LOAD.register((server, level) ->
                ServerLevelEvents.LEVEL_LOAD.invoke().call(level));
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents.UNLOAD.register((server, level) ->
                ServerLevelEvents.LEVEL_UNLOAD.invoke().call(level));
    }
}
