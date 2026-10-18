package com.magicbanana666.morphingtime.core.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.magicbanana666.morphingtime.core.event.common.EntityEvents;
import com.magicbanana666.morphingtime.core.event.common.PlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"DataFlowIssue", "unused"})
@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @ModifyExpressionValue(
            method = "startSleepInBed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z"
            )
    )
    private boolean fixStartSleep(boolean original, @Local(argsOnly = true) BlockPos pos) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        InteractionResult result = PlayerEvents.ALLOW_SLEEP_TIME.invoke().allowSleepTime(player, pos, original);
        if (result == InteractionResult.FAIL) {
            return false;
        }
        if (result == InteractionResult.SUCCESS) {
            return true;
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "startSleepInBed",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;isEmpty()Z"
            )
    )
    private boolean fixMonstersNearby(boolean original, @Local(argsOnly = true) BlockPos pos) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        InteractionResult result = PlayerEvents.ALLOW_MONSTERS_NEARBY.invoke().allowMonstersNearby(player, pos, original);
        if (result == InteractionResult.FAIL) {
            return false;
        }
        if (result == InteractionResult.SUCCESS) {
            return true;
        }
        return original;
    }

    // ServerPlayer overrides die without calling super, so the death event is fired here too
    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void onDie(DamageSource source, CallbackInfo ci) {
        if (EntityEvents.LIVING_DEATH.invoke().die((ServerPlayer) (Object) this, source) == InteractionResult.FAIL) {
            ci.cancel();
        }
    }
}
