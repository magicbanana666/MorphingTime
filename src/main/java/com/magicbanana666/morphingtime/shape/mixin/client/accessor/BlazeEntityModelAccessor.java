package com.magicbanana666.morphingtime.shape.mixin.client.accessor;

import net.minecraft.client.model.monster.blaze.BlazeModel;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(BlazeModel.class)
public interface BlazeEntityModelAccessor {
    @Accessor
    ModelPart[] getUpperBodyParts();
}
