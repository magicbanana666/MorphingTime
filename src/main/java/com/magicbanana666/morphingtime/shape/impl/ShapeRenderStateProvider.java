package com.magicbanana666.morphingtime.shape.impl;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


public interface ShapeRenderStateProvider {
    boolean shape$shapeIsTameable();

    void shape$setShapeIsTameable(boolean isTameable);

    @Nullable EntityRenderState shape$getShapeRenderState();

    void shape$setShapeRenderState(@NotNull Supplier<@Nullable EntityRenderState> shapeRenderState);

    @Nullable EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState> shape$getShapeRenderer();

    void shape$setShapeRenderer(@NotNull Supplier<@Nullable EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState>> shapeRenderer);

    void shape$setInvisRide(boolean invisRide);

    boolean shape$getInvisRide();
}
