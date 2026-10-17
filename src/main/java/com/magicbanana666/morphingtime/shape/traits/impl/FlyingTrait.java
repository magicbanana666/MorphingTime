package com.magicbanana666.morphingtime.shape.traits.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.traits.ShapeTrait;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class FlyingTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final Identifier ID = Shapes.id("flying");
    public static final MapCodec<FlyingTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("slow_falling", true).forGetter(o -> o.slowFalling)
    ).apply(instance, instance.stable(FlyingTrait::new)));

    public FlyingTrait() {
        this.slowFalling = false;
    }

    public FlyingTrait(boolean slowFalling) {
        this.slowFalling = slowFalling;
    }

    public final boolean slowFalling;

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
        ItemStack stack = new ItemStack(Items.ELYTRA);
        graphics.item(stack, x, y);
        return true;
    }
}
