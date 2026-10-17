package com.magicbanana666.morphingtime.shape.mixin.integrations.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@SuppressWarnings("UnresolvedMixinReference")
@Pseudo

@Mixin(targets = "net.bettercombat.client.compat.FirstPersonAnimationCompatibility", remap = false)
public class BetterCombatMixin {
    @WrapOperation(
            method = "firstPersonMode",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/bettercombat/client/compat/FirstPersonAnimationCompatibility;isCameraModPresent:Z",
                    opcode = Opcodes.GETSTATIC
            ),
            require = 0
    )
    private static boolean onFirstPersonRenderCall(Operation<Boolean> original) {
        Player player = Minecraft.getInstance().player;
        if (player != null && PlayerShape.getCurrentShape(player) != null) {
            return true;
        }
        return original.call();
    }
}
