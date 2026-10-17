package com.magicbanana666.morphingtime.shape.ability.impl.specific;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.ability.ShapeAbility;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class AnimationAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final Identifier ID = Shapes.id("animation");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        ((PlayerDataProvider) player).shape$setIsSpecialAnim(!((PlayerDataProvider) player).shape$getIsSpecialAnim());
    }

    @Override
    public Item getIcon() {
        return Items.GRASS_BLOCK;
    }

    @Override
    public int getDefaultCooldown() {
        return 10;
    }
}
