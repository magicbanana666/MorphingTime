package com.magicbanana666.morphingtime.collection.tick;

import com.magicbanana666.morphingtime.core.event.client.ClientTickEvents;
import com.magicbanana666.morphingtime.collection.MorphCollectionClient;
import com.magicbanana666.morphingtime.collection.screen.MorphMenu;
import net.minecraft.client.Minecraft;


public class KeyPressHandler implements ClientTickEvents.Client {
    @Override
    public void tick(Minecraft client) {
        assert client.player != null;

        if (MorphCollectionClient.MENU_KEY.consumeClick()) {
            // Open menu directly - menu access is not restricted by permissions
            // as it can be easily bypassed client-side
            Minecraft.getInstance().gui.setScreen(new MorphMenu());
        }
    }
}
