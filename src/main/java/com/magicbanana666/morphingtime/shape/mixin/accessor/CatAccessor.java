package com.magicbanana666.morphingtime.shape.mixin.accessor;

import net.minecraft.world.entity.animal.feline.Cat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Cat.class)
public interface CatAccessor {

    @Invoker
    void callSetRelaxStateOne(boolean relaxStateOne);
}
