package com.magicbanana666.morphingtime.shape.api.model;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;


@FunctionalInterface
public interface ClassArmProvider<T extends Model> {
    ModelPart getArm(LivingEntity entity, T model);
}
