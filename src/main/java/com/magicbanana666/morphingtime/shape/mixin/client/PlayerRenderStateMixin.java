package com.magicbanana666.morphingtime.shape.mixin.client;

import com.magicbanana666.morphingtime.core.util.MemoizingSupplier;
import com.magicbanana666.morphingtime.shape.impl.ShapeRenderStateProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Supplier;


@Mixin(AvatarRenderState.class)
public class PlayerRenderStateMixin implements ShapeRenderStateProvider {
    @Unique
    private boolean shape$shapeIsTameable = false;
    @Unique
    private @NotNull Supplier<EntityRenderState> shape$shapeRenderState = () -> null;
    @Unique
    private @NotNull Supplier<EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState>> shape$shapeRenderer = () -> null;
    @Unique
    private boolean shape$invisRide = false;

    @Override
    public boolean shape$shapeIsTameable() {
        return shape$shapeIsTameable;
    }

    @Override
    public void shape$setShapeIsTameable(boolean isTameable) {
        this.shape$shapeIsTameable = isTameable;
    }

    @Override
    public @Nullable EntityRenderState shape$getShapeRenderState() {
        return shape$shapeRenderState.get();
    }

    @Override
    public void shape$setShapeRenderState(@NotNull Supplier<@Nullable EntityRenderState> shapeRenderState) {
        this.shape$shapeRenderState = new MemoizingSupplier<>(shapeRenderState);
    }

    @Unique
    @Override
    public @Nullable EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState> shape$getShapeRenderer() {
        return shape$shapeRenderer.get();
    }

    @Unique
    @Override
    public void shape$setShapeRenderer(@NotNull Supplier<@Nullable EntityRenderer<@NotNull LivingEntity, @NotNull EntityRenderState>> shapeRenderer) {
        shape$shapeRenderer = new MemoizingSupplier<>(shapeRenderer);
    }

    @Override
    public void shape$setInvisRide(boolean invisRide) {
        this.shape$invisRide = invisRide;
    }

    @Override
    public boolean shape$getInvisRide() {
        return this.shape$invisRide;
    }
}
