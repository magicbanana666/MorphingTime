package com.magicbanana666.morphingtime.core.event.client;

import com.magicbanana666.morphingtime.core.event.Event;
import com.magicbanana666.morphingtime.core.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings({"unused", "SameReturnValue"})

public final class RenderEvents {
    public static final Event<HUDRendering> HUD_RENDERING = EventFactory.createWithVoid();
    public static final Event<OverlayRendering> RENDER_HEALTH = EventFactory.createWithInteractionResult();
    public static final Event<OverlayRendering> RENDER_FOOD = EventFactory.createWithInteractionResult();
    public static final Event<OverlayRendering> RENDER_BREATH = EventFactory.createWithInteractionResult();
    public static final Event<OverlayRendering> RENDER_MOUNT_HEALTH = EventFactory.createWithInteractionResult();

    
    @FunctionalInterface
    public interface HUDRendering {
        void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker tickCounter);
    }

    
    @FunctionalInterface
    public interface OverlayRendering {
        InteractionResult render(@Nullable GuiGraphicsExtractor graphics, Player player);
    }
}
