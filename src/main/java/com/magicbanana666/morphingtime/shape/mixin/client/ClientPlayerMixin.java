package com.magicbanana666.morphingtime.shape.mixin.client;

import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.model.EntityUpdater;
import com.magicbanana666.morphingtime.shape.api.model.EntityUpdaters;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(AbstractClientPlayer.class)
public class ClientPlayerMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "tick", at = @At("RETURN"))
    private void fixEntityAnimations(CallbackInfo ci) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        if (shape != null) {
            EntityUpdater<LivingEntity> entityUpdater = EntityUpdaters.getUpdater((EntityType<@NotNull LivingEntity>) shape.getType());
            if (entityUpdater != null) {
                entityUpdater.update(player, shape);
            }
        }
    }
}
