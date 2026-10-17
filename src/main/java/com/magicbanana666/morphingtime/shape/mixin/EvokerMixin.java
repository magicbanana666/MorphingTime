package com.magicbanana666.morphingtime.shape.mixin;

import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import com.magicbanana666.morphingtime.shape.impl.ShapeDataProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpellcasterIllager.class)
public class EvokerMixin {
    @Inject(method = "isCastingSpell", at = @At("HEAD"), cancellable = true)
    public void castAnimation(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof ShapeDataProvider shapeData) {
            Entity player = entity.level().getEntity(shapeData.shape$shapedPlayer());
            if (player instanceof PlayerDataProvider playerData) {
                cir.setReturnValue(playerData.shape$getIsSpecialAnim());
            }
        }
    }
}
