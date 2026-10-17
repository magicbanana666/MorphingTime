package com.magicbanana666.morphingtime.shape.mixin.accessor;

import net.minecraft.world.entity.animal.cow.MushroomCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MushroomCow.class)
public interface MushroomCowAccessor {
    @Invoker
    void callSetVariant(MushroomCow.Variant variant);
}
