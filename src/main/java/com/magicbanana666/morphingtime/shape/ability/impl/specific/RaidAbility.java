package com.magicbanana666.morphingtime.shape.ability.impl.specific;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.ability.ShapeAbility;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class RaidAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final Identifier ID = Shapes.id("raid");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        if (world instanceof ServerLevel serverLevel) {
            serverLevel.getRaids().createOrExtendRaid(player, player.getOnPos());
            player.playSound(SoundEvents.RAID_HORN.value());
        }
    }

    @Override
    public int getDefaultCooldown() {
        return 2400;
    }

    @Override
    public Item getIcon() {
        return Items.CROSSBOW;
    }
}
