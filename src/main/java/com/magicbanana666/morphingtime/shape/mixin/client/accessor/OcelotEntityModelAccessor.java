package com.magicbanana666.morphingtime.shape.mixin.client.accessor;

import net.minecraft.client.model.animal.feline.AbstractFelineModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(AbstractFelineModel.class)
public interface OcelotEntityModelAccessor {
    @Accessor
    ModelPart getRightFrontLeg();
}
