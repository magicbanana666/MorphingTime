package com.magicbanana666.morphingtime.shape.ability.impl.specific;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.ability.ShapeAbility;
import com.magicbanana666.morphingtime.shape.impl.SonicBoomUser;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class WardenAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final Identifier ID = Shapes.id("warden");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        ((SonicBoomUser) player).shape$ability_startSonicBoom();
    }

    @Override
    public Item getIcon() {
        return Items.ECHO_SHARD;
    }

    @Override
    public int getDefaultCooldown() {
        return 200;
    }
}
