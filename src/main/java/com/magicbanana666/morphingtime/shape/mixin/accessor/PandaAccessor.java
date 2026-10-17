package com.magicbanana666.morphingtime.shape.mixin.accessor;

import net.minecraft.world.entity.animal.panda.Panda;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Panda.class)
public interface PandaAccessor {

    @Invoker
    void callUpdateSitAmount();
}
