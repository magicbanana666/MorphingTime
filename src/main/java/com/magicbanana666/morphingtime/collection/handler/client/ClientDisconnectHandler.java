package com.magicbanana666.morphingtime.collection.handler.client;

import com.magicbanana666.morphingtime.core.event.client.ClientTickEvents;
import com.magicbanana666.morphingtime.collection.screen.EntityRenderCache;
import net.minecraft.client.Minecraft;

/**
 * Handles clearing the entity render cache when the player disconnects from a world.
 */
public class ClientDisconnectHandler implements ClientTickEvents.Client {

    private boolean wasInWorld = false;

    @Override
    public void tick(Minecraft client) {
        boolean isInWorld = client.level != null && client.player != null;

        // Detect transition from in-world to not-in-world (disconnect)
        if (wasInWorld && !isInWorld) {
            EntityRenderCache.clearCache();
        }

        wasInWorld = isInWorld;
    }
}
