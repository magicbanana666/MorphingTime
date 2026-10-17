package com.magicbanana666.morphingtime.shape.mixin.client.accessor;

import net.minecraft.client.renderer.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(EntityRenderer.class)
public interface EntityShadowAccessor {
    @Accessor
    float getShadowRadius();
}
