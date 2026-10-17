package com.magicbanana666.morphingtime.shape.traits.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.traits.ShapeTrait;
import net.minecraft.data.AtlasIds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class BurnInDaylightTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final Identifier ID = Shapes.id("burn_in_daylight");
    public static final MapCodec<BurnInDaylightTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("burn_in_moonlight_instead", false).forGetter(o -> o.burnInMoonlightInstead)
    ).apply(instance, instance.stable(BurnInDaylightTrait::new)));

    public BurnInDaylightTrait() {
        this.burnInMoonlightInstead = false;
    }

    public BurnInDaylightTrait(boolean burnInMoonlightInstead) {
        this.burnInMoonlightInstead = burnInMoonlightInstead;
    }

    public final boolean burnInMoonlightInstead;

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }

    
    @Override
    public boolean renderIcon(RenderPipeline pipeline, @NotNull GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(Identifier.withDefaultNamespace("block/fire_0"));
        graphics.blitSprite(pipeline, sprite, x, y, width, height);
        return true;
    }
}
