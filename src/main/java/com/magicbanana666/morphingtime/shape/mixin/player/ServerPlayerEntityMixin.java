package com.magicbanana666.morphingtime.shape.mixin.player;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.FlightHelper;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.PlayerShapeChanger;
import com.magicbanana666.morphingtime.shape.traits.TraitRegistry;
import com.magicbanana666.morphingtime.shape.traits.impl.AttackForHealthTrait;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"ConstantValue", "RedundantCast", "DataFlowIssue"})
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

    public ServerPlayerEntityMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void revoke2ndShapeOnDeath(DamageSource source, CallbackInfo ci) {
        if (Shapes.CONFIG.revoke2ndShapeOnDeath && !this.isCreative() && !((ServerPlayer) (Object) this).isSpectator()) {
            PlayerShapeChanger.change2ndShape((ServerPlayer) (Object) this, null);
        }
    }

    @Inject(method = "initInventoryMenu()V", at = @At("HEAD"))
    private void onSpawn(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (Shapes.hasFlyingPermissions(player)) {
            if (!FlightHelper.hasFlight(player)) {
                FlightHelper.grantFlightTo(player);
                FlightHelper.updateFlyingSpeed(this);
                onUpdateAbilities();
            }

            FlightHelper.grantFlightTo(player);
        }
    }

    @Inject(method = "disconnect", at = @At("HEAD"))
    private void disconnectInject(CallbackInfo ci) {
        if (this.isPassenger()) {
            if (this.getVehicle() instanceof Player)
                this.stopRiding();
        }
    }

    @WrapWithCondition(method = "doTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private boolean preventFoodDataTick(FoodData instance, ServerPlayer player) {
        LivingEntity shape = PlayerShape.getCurrentShape(player);
        return player.hasEffect(MobEffects.SATURATION) || !TraitRegistry.has(shape, AttackForHealthTrait.ID);
    }
}
