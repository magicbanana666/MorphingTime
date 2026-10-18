package com.magicbanana666.morphingtime.shape.fabric.mixin.client;

import com.magicbanana666.morphingtime.shape.impl.ShapeAvatarRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// The player renderer doesn't override submit in vanilla, so the shape is drawn from here instead of the player model
@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD"), cancellable = true)
    private void submitShape(LivingEntityRenderState state, PoseStack matrixStack, SubmitNodeCollector buffer, CameraRenderState camera, CallbackInfo ci) {
        if (this instanceof ShapeAvatarRenderer renderer && state instanceof AvatarRenderState avatarState
                && renderer.shape$submitShape(avatarState, matrixStack, buffer, camera)) {
            ci.cancel();
        }
    }
}
