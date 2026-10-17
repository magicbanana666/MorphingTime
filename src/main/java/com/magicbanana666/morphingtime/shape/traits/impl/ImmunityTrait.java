package com.magicbanana666.morphingtime.shape.traits.impl;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.traits.ShapeTrait;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public class ImmunityTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final Identifier ID = Shapes.id("immunity");
    public static final MapCodec<ImmunityTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Identifier.CODEC.fieldOf("effect").forGetter(o -> BuiltInRegistries.MOB_EFFECT.getKey(o.effect))
    ).apply(instance, instance.stable((effect) -> new ImmunityTrait<>(BuiltInRegistries.MOB_EFFECT.get(effect).orElseThrow().value()))));

    public final MobEffect effect;

    public ImmunityTrait(MobEffect effect) {
        this.effect = effect;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }
}
