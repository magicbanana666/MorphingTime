package com.magicbanana666.morphingtime.core.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.magicbanana666.morphingtime.core.event.common.PlayerEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"DataFlowIssue", "unused"})
@Mixin(Player.class)
public class PlayerMixin {
    // Whether a sleeping player can continue sleeping
    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z"
            )
    )
    private boolean continueSleeping(boolean original) {
        Player player = (Player) (Object) this;
        InteractionResult result = PlayerEvents.ALLOW_SLEEP_TIME.invoke().allowSleepTime(player, player.getSleepingPos().orElse(null), original);
        if (result == InteractionResult.FAIL) {
            return false;
        }
        if (result == InteractionResult.SUCCESS) {
            return true;
        }
        return original;
    }

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    private void destroySpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(PlayerEvents.DESTROY_SPEED.invoke().setDestroySpeed((Player) (Object) this, cir.getReturnValueF()));
    }
}
