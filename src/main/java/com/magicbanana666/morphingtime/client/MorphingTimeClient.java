package com.magicbanana666.morphingtime.client;

import com.magicbanana666.morphingtime.UnlockEffectPayload;
import com.magicbanana666.morphingtime.client.animation.UnlockGhosts;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

/** Client side setup: the unlock animation. The client side of the modules is initialized by the main entry point. */
public final class MorphingTimeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(UnlockEffectPayload.TYPE, (payload, context) -> UnlockGhosts.start(payload.entityId()));
        LevelRenderEvents.COLLECT_SUBMITS.register(UnlockGhosts::onCollectSubmits);
    }
}
