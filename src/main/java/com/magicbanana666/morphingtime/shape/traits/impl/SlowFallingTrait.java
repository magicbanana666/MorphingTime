package com.magicbanana666.morphingtime.shape.traits.impl;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.traits.ShapeTrait;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SlowFallingTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final Identifier ID = Shapes.id("slow_falling");
    public static final MapCodec<SlowFallingTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new SlowFallingTrait<>()));

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    @Override
    public boolean canBeRegisteredMultipleTimes() {
        return false;
    }

    
    @Override
    public boolean renderIcon(RenderPipeline pipeline, @NotNull GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Hud.getMobEffectSprite(MobEffects.SLOW_FALLING), x, y, width, height);
        return true;
    }
}
