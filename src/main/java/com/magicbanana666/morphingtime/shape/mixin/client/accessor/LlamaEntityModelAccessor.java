package com.magicbanana666.morphingtime.shape.mixin.client.accessor;

import net.minecraft.client.model.animal.llama.LlamaModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(LlamaModel.class)
public interface LlamaEntityModelAccessor {
    @Accessor
    ModelPart getRightFrontLeg();
}
