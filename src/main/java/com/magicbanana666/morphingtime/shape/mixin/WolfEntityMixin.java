package com.magicbanana666.morphingtime.shape.mixin;

import com.magicbanana666.morphingtime.shape.Shapes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"RedundantCast"})
@Mixin(Wolf.class)
public abstract class WolfEntityMixin extends TamableAnimal {
    @Unique
    private static final Identifier shape$SPECIAL_WILD = Shapes.id("textures/entity/wolf/special_wild.png");
    @Unique
    private static final Identifier shape$SPECIAL_TAMED = Shapes.id("textures/entity/wolf/special_tame.png");
    @Unique
    private static final Identifier shape$SPECIAL_ANGRY = Shapes.id("textures/entity/wolf/special_angry.png");


    private WolfEntityMixin(EntityType<? extends TamableAnimal> entityType, Level world) {
        super(entityType, world);
    }

    // Stored in a data attachment so the vanilla wolf's synced entity data stays untouched
    @Unique
    private boolean shape$isSpecial() {
        return ((Entity) (Object) this).getData(com.magicbanana666.morphingtime.MorphingTimeAttachments.SPECIAL_WOLF);
    }

    @Unique
    private void shape$setSpecial(boolean special) {
        if (special != this.shape$isSpecial()) {
            ((Entity) (Object) this).setData(com.magicbanana666.morphingtime.MorphingTimeAttachments.SPECIAL_WOLF, special);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (this.hasCustomName()) {
            if (this.getCustomName().getString().equalsIgnoreCase("Patreon")) {
                this.shape$setSpecial(true);
            } else {
                // reset texture on rename
                this.shape$setSpecial(false);
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void onWriteCustomDataToNbt(ValueOutput out, CallbackInfo ci) {
        if (this.shape$isSpecial()) {
            out.putBoolean("isSpecial", true);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void onReadCustomDataFromNbt(ValueInput in, CallbackInfo ci) {
        this.shape$setSpecial(in.getBooleanOr("isSpecial", false));
    }

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void setSpecialTexture(CallbackInfoReturnable<Identifier> cir) {
        if (this.shape$isSpecial()) {
            if (this.isTame()) {
                cir.setReturnValue(shape$SPECIAL_TAMED);
            } else {
                cir.setReturnValue(((Wolf) (Object) this).isAngry() ? shape$SPECIAL_ANGRY : shape$SPECIAL_WILD);
            }
        }
    }
}
