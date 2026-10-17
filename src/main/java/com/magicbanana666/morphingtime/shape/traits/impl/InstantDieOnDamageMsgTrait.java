package com.magicbanana666.morphingtime.shape.traits.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.traits.ShapeTrait;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public class InstantDieOnDamageMsgTrait<E extends LivingEntity> extends ShapeTrait<E> {
    public static final Identifier ID = Shapes.id("instant_die_on_damage_msg");
    public static final MapCodec<InstantDieOnDamageMsgTrait<?>> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            Codec.STRING.fieldOf("msgId").forGetter(o -> o.msgId)
    ).apply(instance, instance.stable(InstantDieOnDamageMsgTrait::new)));

    public InstantDieOnDamageMsgTrait(String msgId) {
        this.msgId = msgId;
    }

    public final String msgId;

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public MapCodec<? extends ShapeTrait<?>> codec() {
        return CODEC;
    }
}
