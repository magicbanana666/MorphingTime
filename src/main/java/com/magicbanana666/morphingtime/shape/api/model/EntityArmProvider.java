package com.magicbanana666.morphingtime.shape.api.model;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;


@FunctionalInterface
public interface EntityArmProvider<T extends LivingEntity, R extends LivingEntityRenderState> {
    ModelPart getArm(T entity, EntityModel<R> model);
}
