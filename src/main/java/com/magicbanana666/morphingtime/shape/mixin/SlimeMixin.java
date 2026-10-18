package com.magicbanana666.morphingtime.shape.mixin;

import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.cubemob.AbstractCubeMob;
import net.minecraft.world.entity.monster.cubemob.MagmaCube;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// playerTouch moved from Slime to AbstractCubeMob, and magma cubes no longer extend Slime
@Mixin(AbstractCubeMob.class)
public class SlimeMixin {
    @Inject(method = "playerTouch", at = @At(value = "HEAD"), cancellable = true)
    private void onTouchPlayer(Player player, CallbackInfo ci) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (isSlime(this) && isSlime(shape)) {
            ci.cancel();
        }
    }

    private static boolean isSlime(Object entity) {
        return entity instanceof Slime || entity instanceof MagmaCube;
    }
}
