package com.magicbanana666.morphingtime.collection.handler;

import com.magicbanana666.morphingtime.collection.impl.PlayerMorph;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.events.ShapeEvents;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SwapShapeCallback implements ShapeEvents.ShapeSwapCallback {
    @SuppressWarnings("unchecked")
    @Override
    public InteractionResult swap(ServerPlayer player, @Nullable LivingEntity to) {
        if (to != null) {
            ShapeType<?> toType = ShapeType.from(to);
            LivingEntity previous = PlayerShape.getCurrentShape(player);
            EntityType<? extends @NotNull LivingEntity> toEType = (EntityType<? extends @NotNull LivingEntity>) to.getType();
            if (previous == null || !Objects.equals(previous.getType(), toEType)) {
                PlayerMorph.handleSwap(player, toEType);
            } else {
                PlayerMorph.setDefaultVariant(player, previous.getType(), toType);
            }
        }
        return InteractionResult.PASS;
    }
}
