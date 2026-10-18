package com.magicbanana666.morphingtime.shape.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.ShapesClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)

public class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ScrollWheelHandler;getNextScrollWheelSelection(DII)I"))
    private int handleScrollInVariantsMenu(double direction, int current, int size, Operation<Integer> original) {
        if (!minecraft.gui.hud.isHidden() && ShapesClient.isRenderingVariantsMenu && Shapes.CONFIG.unlockEveryVariant && minecraft.gui.screen() == null) {
            ShapesClient.variantOffset -= (int) direction;
            return current;
        } else {
            return original.call(direction, current, size);
        }
    }
}
