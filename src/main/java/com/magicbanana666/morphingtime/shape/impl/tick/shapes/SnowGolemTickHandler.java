package com.magicbanana666.morphingtime.shape.impl.tick.shapes;

import com.magicbanana666.morphingtime.shape.api.ShapeTickHandler;
import net.minecraft.world.entity.animal.golem.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SnowGolemTickHandler implements ShapeTickHandler<SnowGolem> {

    @Override
    public void tick(Player player, SnowGolem golem) {
        if (player.isCrouching() && player.onGround() && player.level().getBlockState(player.blockPosition()).isAir()) {
            BlockState defaultSnowBlockState = Blocks.SNOW.defaultBlockState();
            player.level().setBlockAndUpdate(player.blockPosition(), defaultSnowBlockState);
        }
    }
}
