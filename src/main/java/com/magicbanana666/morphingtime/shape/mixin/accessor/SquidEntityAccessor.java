package com.magicbanana666.morphingtime.shape.mixin.accessor;

import net.minecraft.world.entity.animal.squid.Squid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Squid.class)
public interface SquidEntityAccessor {

    @Accessor
    float getTentacleSpeed();

    @Accessor
    void setTentacleSpeed(float tentacleSpeed);
}
