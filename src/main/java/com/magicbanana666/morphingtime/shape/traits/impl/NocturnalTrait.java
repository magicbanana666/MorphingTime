package com.magicbanana666.morphingtime.shape.traits.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.traits.ShapeTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public class NocturnalTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final Identifier ID = Shapes.id("nocturnal");
    public static final MapCodec<NocturnalTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.stable(new NocturnalTrait<>()));

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
}
