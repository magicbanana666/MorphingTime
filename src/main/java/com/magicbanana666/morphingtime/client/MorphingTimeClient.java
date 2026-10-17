package com.magicbanana666.morphingtime.client;

import com.magicbanana666.morphingtime.UnlockEffectPayload;
import com.magicbanana666.morphingtime.client.animation.UnlockGhosts;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.common.NeoForge;

/** Client side setup: the unlock animation. */
public final class MorphingTimeClient {

    private MorphingTimeClient() {
    }

    public static void init(IEventBus modBus) {
        modBus.addListener(RegisterClientPayloadHandlersEvent.class, event ->
                event.register(UnlockEffectPayload.TYPE, (payload, context) -> UnlockGhosts.start(payload.entityId())));
        NeoForge.EVENT_BUS.addListener(UnlockGhosts::onSubmitCustomGeometry);
    }
}
