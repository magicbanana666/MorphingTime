package com.magicbanana666.morphingtime.shape.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

/** Implemented by the player renderer: draws the shape of a morphed player instead of the player model. */
public interface ShapeAvatarRenderer {
    /**
     * @return true when the shape was drawn (or nothing has to be drawn) and the player model must be skipped
     */
    boolean shape$submitShape(AvatarRenderState state, PoseStack matrixStack, SubmitNodeCollector buffer, CameraRenderState camera);
}
