package com.magicbanana666.morphingtime.shape.mixin.accessor;

import net.minecraft.world.entity.animal.equine.Llama;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Llama.class)
public interface LlamaAccessor {
    @Invoker
    void callSetVariant(Llama.Variant variant);
}
