package com.magicbanana666.morphingtime.collection;

import com.mojang.blaze3d.platform.InputConstants;
import com.magicbanana666.morphingtime.core.event.client.ClientPlayerEvents;
import com.magicbanana666.morphingtime.core.event.client.ClientTickEvents;
import com.magicbanana666.morphingtime.core.registration.KeyBindingRegistry;
import com.magicbanana666.morphingtime.collection.handler.client.ClientDisconnectHandler;
import com.magicbanana666.morphingtime.collection.handler.client.ClientPlayerRespawnHandler;
import com.magicbanana666.morphingtime.collection.handler.client.EntityRenderCacheHandler;
import com.magicbanana666.morphingtime.collection.network.ClientNetworking;
import com.magicbanana666.morphingtime.collection.tick.KeyPressHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;


public class MorphCollectionClient {
    public static final KeyMapping.Category MORPH_MENU_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("remorphed", "category"));
    public static final KeyMapping MENU_KEY = new KeyMapping("key.remorphed_menu", InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K, MORPH_MENU_CATEGORY);

    public void initialize() {
        KeyBindingRegistry.register(MENU_KEY);

        // Register event handlers
        ClientTickEvents.CLIENT_PRE.register(new KeyPressHandler());
        ClientTickEvents.CLIENT_PRE.register(new ClientDisconnectHandler());
        ClientTickEvents.CLIENT_PRE.register(new EntityRenderCacheHandler());
        ClientNetworking.registerPacketHandlers();

        ClientPlayerEvents.CLIENT_PLAYER_RESPAWN.register(new ClientPlayerRespawnHandler());
    }
}
