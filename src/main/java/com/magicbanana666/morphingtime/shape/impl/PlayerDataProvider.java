package com.magicbanana666.morphingtime.shape.impl;

import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface PlayerDataProvider {

    @Nullable
    ShapeType<?> shape$get2ndShape();

    void shape$set2ndShape(ShapeType<?> unlocked);

    int shape$getRemainingHostilityTime();

    void shape$setRemainingHostilityTime(int max);

    int shape$getAbilityCooldown();

    void shape$setAbilityCooldown(int cooldown);

    @Nullable
    LivingEntity shape$getCurrentShape();

    void shape$setCurrentShape(@Nullable LivingEntity shape);

    void shape$updateShapes(@Nullable LivingEntity shape);

    void shape$setIsSpecialAnim(boolean inAnimation);

    boolean shape$getIsSpecialAnim();
}
