package com.magicbanana666.morphingtime.core.fabric.client;

import com.magicbanana666.morphingtime.core.MorphCore;
import com.magicbanana666.morphingtime.core.event.client.ClientPlayerEvents;
import com.magicbanana666.morphingtime.core.event.client.ClientTickEvents;
import com.magicbanana666.morphingtime.core.event.client.RenderEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
@ApiStatus.Internal

public final class CoreFabricEventHandlerClient {
    private CoreFabricEventHandlerClient() {
    }

    public static void register() {
        // drawn after all other HUD elements
        HudElementRegistry.addLast(MorphCore.id("hud"), (graphics, deltaTracker) ->
                RenderEvents.HUD_RENDERING.invoke().extractRenderState(graphics, deltaTracker));

        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.START_CLIENT_TICK.register(minecraft ->
                ClientTickEvents.CLIENT_PRE.invoke().tick(minecraft));
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(minecraft ->
                ClientTickEvents.CLIENT_POST.invoke().tick(minecraft));

        replaceOverlay(VanillaHudElements.HEALTH_BAR, RenderEvents.RENDER_HEALTH);
        replaceOverlay(VanillaHudElements.FOOD_BAR, RenderEvents.RENDER_FOOD);
        replaceOverlay(VanillaHudElements.AIR_BAR, RenderEvents.RENDER_BREATH);
        replaceOverlay(VanillaHudElements.MOUNT_HEALTH, RenderEvents.RENDER_MOUNT_HEALTH);

        ClientPlayConnectionEvents.DISCONNECT.register((listener, minecraft) ->
                ClientPlayerEvents.CLIENT_PLAYER_QUIT.invoke().quit(minecraft.player));
    }

    // the vanilla element is skipped when a listener returns FAIL
    private static void replaceOverlay(Identifier element, com.magicbanana666.morphingtime.core.event.Event<RenderEvents.OverlayRendering> event) {
        HudElementRegistry.replaceElement(element, original -> (HudElement) (graphics, deltaTracker) -> {
            InteractionResult result = event.invoke().render(graphics, Minecraft.getInstance().player);
            if (result != InteractionResult.FAIL) {
                original.extractRenderState(graphics, deltaTracker);
            }
        });
    }
}
