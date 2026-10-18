package com.magicbanana666.morphingtime.shape;

import com.mojang.blaze3d.platform.InputConstants;
import com.magicbanana666.morphingtime.core.event.client.ClientPlayerEvents;
import com.magicbanana666.morphingtime.core.event.client.ClientTickEvents;
import com.magicbanana666.morphingtime.core.event.client.RenderEvents;
import com.magicbanana666.morphingtime.core.registration.KeyBindingRegistry;
import com.magicbanana666.morphingtime.shape.ability.AbilityOverlayRenderer;
import com.magicbanana666.morphingtime.shape.api.model.EntityArms;
import com.magicbanana666.morphingtime.shape.api.model.EntityUpdaters;
import com.magicbanana666.morphingtime.shape.api.platform.ApiLevel;
import com.magicbanana666.morphingtime.shape.eventhandler.ClientRespawnHandler;
import com.magicbanana666.morphingtime.shape.impl.tick.KeyPressHandler;
import com.magicbanana666.morphingtime.shape.network.ClientNetworking;
import com.magicbanana666.morphingtime.shape.screen.hud.OverlayEventHandler;
import com.magicbanana666.morphingtime.shape.screen.hud.VariantMenu;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;


public class ShapesClient {
    public static boolean isRenderingVariantsMenu = false;
    @ApiStatus.Internal
    public static int variantOffset = 0;

    public static final KeyMapping.Category MORPH_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("walkers", "category"));
    public static final KeyMapping UNLOCK_KEY = new KeyMapping("key.walkers_unlock", InputConstants.Type.KEYBOARD, InputConstants.KEY_U, MORPH_CATEGORY);
    public static final KeyMapping TRANSFORM_KEY = new KeyMapping("key.walkers", InputConstants.Type.KEYBOARD, InputConstants.KEY_R, MORPH_CATEGORY);
    public static final KeyMapping ABILITY_KEY = new KeyMapping("key.walkers_ability", InputConstants.Type.KEYBOARD, InputConstants.KEY_V, MORPH_CATEGORY);
    public static final KeyMapping VARIANTS_MENU_KEY = new KeyMapping("key.walkers_variants", InputConstants.Type.KEYBOARD, InputConstants.KEY_J, MORPH_CATEGORY);


    public void initialize() {
        KeyBindingRegistry.register(ABILITY_KEY);
        KeyBindingRegistry.register(TRANSFORM_KEY);
        KeyBindingRegistry.register(UNLOCK_KEY);
        KeyBindingRegistry.register(VARIANTS_MENU_KEY);

        // Register client-side event handlers
        EntityUpdaters.init();
        AbilityOverlayRenderer.register();
        EntityArms.init();

        // Register event handlers
        ClientTickEvents.CLIENT_PRE.register(new KeyPressHandler());
        RenderEvents.HUD_RENDERING.register(new VariantMenu());
        ClientNetworking.registerPacketHandlers();

        OverlayEventHandler.initialize();

        ClientPlayerEvents.CLIENT_PLAYER_RESPAWN.register(new ClientRespawnHandler());

        ClientPlayerEvents.CLIENT_PLAYER_QUIT.register(player -> {
            if (player != null && ApiLevel.getClientLevel() != null) {
                ApiLevel.ON_API_LEVEL_CHANGE_EVENT.invoke().onApiLevelChange(ApiLevel.getClientLevel());
            }
        });
    }
}
