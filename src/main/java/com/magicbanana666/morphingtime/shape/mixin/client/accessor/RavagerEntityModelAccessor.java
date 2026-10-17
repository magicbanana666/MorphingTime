package com.magicbanana666.morphingtime.shape.mixin.client.accessor;

import net.minecraft.client.model.monster.ravager.RavagerModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(RavagerModel.class)
public interface RavagerEntityModelAccessor {
    @Accessor
    ModelPart getRightFrontLeg();
}
