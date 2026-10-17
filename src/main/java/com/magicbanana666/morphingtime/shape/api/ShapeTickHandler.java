package com.magicbanana666.morphingtime.shape.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface ShapeTickHandler<Z extends Entity> {

    void tick(Player player, Z entity);
}
