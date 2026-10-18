package com.magicbanana666.morphingtime.core.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.magicbanana666.morphingtime.core.event.common.EntityEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"DataFlowIssue", "unused"})
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    protected boolean dead;

    @Shadow
    protected abstract int decreaseAirSupply(int airSupply);

    @Shadow
    protected abstract int increaseAirSupply(int airSupply);

    @Shadow
    protected abstract boolean shouldTakeDrowningDamage();

    @Shadow
    public abstract boolean canBreatheUnderwater();

    private LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    private void onDie(DamageSource source, CallbackInfo ci) {
        if (!this.isRemoved() && !this.dead) {
            if (EntityEvents.LIVING_DEATH.invoke().die((LivingEntity) (Object) this, source) == InteractionResult.FAIL) {
                ci.cancel();
            }
        }
    }

    /*
     * Breathing: the vanilla air supply logic of baseTick is replaced here, so the LIVING_BREATHE event decides
     * whether the entity can breathe (also on land, EG for fish shapes). The vanilla water check then returns false
     * and the vanilla refill on land is skipped, so only this logic changes the air supply.
     */
    @WrapOperation(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z", ordinal = 0))
    private boolean handleBreathing(LivingEntity entity, TagKey<Fluid> fluid, Operation<Boolean> original) {
        if (!(entity.level() instanceof ServerLevel level) || fluid != FluidTags.WATER) {
            return original.call(entity, fluid);
        }

        int airSupply = this.getAirSupply();
        int consumeAirAmount = airSupply - this.decreaseAirSupply(airSupply);
        int refillAirAmount = this.increaseAirSupply(airSupply) - airSupply;

        boolean isAir = !original.call(entity, fluid) || level.getBlockState(BlockPos.containing(this.getX(), this.getEyeY(), this.getZ())).is(Blocks.BUBBLE_COLUMN);
        boolean canBreathe = isAir;
        // These keep the entity from drowning, but under water the air supply only refills from effects
        if (!isAir && (this.canBreatheUnderwater() || MobEffectUtil.hasWaterBreathing(entity) || (entity instanceof Player player && player.getAbilities().invulnerable))) {
            canBreathe = true;
            if (!MobEffectUtil.shouldEffectsRefillAirsupply(entity)) {
                refillAirAmount = 0;
            }
        }

        canBreathe = EntityEvents.LIVING_BREATHE.invoke().breathe(entity, canBreathe);
        if (canBreathe) {
            this.setAirSupply(Math.min(this.getAirSupply() + refillAirAmount, this.getMaxAirSupply()));
        } else {
            this.setAirSupply(this.getAirSupply() - consumeAirAmount);
        }

        if (this.shouldTakeDrowningDamage()) {
            this.setAirSupply(0);
            level.broadcastEntityEvent(this, (byte) 67);
            this.hurtServer(level, this.damageSources().drown(), 2.0F);
        }

        if (!isAir && this.isPassenger() && this.getVehicle() != null && this.getVehicle().dismountsUnderwater()) {
            this.stopRiding();
        }
        return false;
    }

    // The vanilla refill on land, already done above
    @ModifyExpressionValue(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMaxAirSupply()I", ordinal = 1))
    private int skipVanillaRefill(int original) {
        return this.level() instanceof ServerLevel ? Integer.MIN_VALUE : original;
    }
}
