package com.magicbanana666.morphingtime.shape.screen.hud;

import com.magicbanana666.morphingtime.core.event.client.RenderEvents;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.traits.TraitRegistry;
import com.magicbanana666.morphingtime.shape.traits.impl.AttackForHealthTrait;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class OverlayEventHandler {
    public static void initialize() {
        RenderEvents.RENDER_FOOD.register(new RenderFood());
    }

    private static class RenderFood implements RenderEvents.OverlayRendering {
        @Override
        public InteractionResult render(@Nullable GuiGraphicsExtractor graphics, Player player) {
            if (player != null) {
                LivingEntity shape = PlayerShape.getCurrentShape(player);

                if (shape != null) {
                    if (TraitRegistry.has(shape, AttackForHealthTrait.ID)) {
                        return InteractionResult.FAIL;
                    }
                }
            }

            return InteractionResult.PASS;
        }
    }
}
