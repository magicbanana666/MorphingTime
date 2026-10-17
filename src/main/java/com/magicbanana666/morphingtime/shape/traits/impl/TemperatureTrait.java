package com.magicbanana666.morphingtime.shape.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.traits.ShapeTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public class TemperatureTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final Identifier ID = Shapes.id("temperature");
    public static final MapCodec<TemperatureTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.BOOL.optionalFieldOf("cold_enough_to_snow", true).forGetter(o -> o.coldEnoughToSnow)
    ).apply(instance, instance.stable(TemperatureTrait::new)));

    public final boolean coldEnoughToSnow;


    public TemperatureTrait() {
        this(true);
    }

    /**
     * Damage the player if they aren't in an area which is cold enough to snow (or warm enough to rain, if "coldEnoughToSnow" is "false")
     */
    public TemperatureTrait(boolean coldEnoughToSnow) {
        this.coldEnoughToSnow = coldEnoughToSnow;
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
