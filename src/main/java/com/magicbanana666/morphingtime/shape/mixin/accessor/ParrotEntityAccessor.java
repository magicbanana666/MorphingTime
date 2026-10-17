package com.magicbanana666.morphingtime.shape.mixin.accessor;

import net.minecraft.world.entity.animal.parrot.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Parrot.class)
public interface ParrotEntityAccessor {
    @Invoker
    void callCalculateFlapping();
}
