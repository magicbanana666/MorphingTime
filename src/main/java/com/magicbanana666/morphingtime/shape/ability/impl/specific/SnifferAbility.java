package com.magicbanana666.morphingtime.shape.ability.impl.specific;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.ability.ShapeAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class SnifferAbility<T extends LivingEntity> extends ShapeAbility<T> {
    public static final Identifier ID = Shapes.id("sniffer");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void onUse(ServerPlayer player, T shape, ServerLevel world) {
        // Ensure the player isn't in water or lava and is on the ground
        if (player.isInLava() || player.isInWater() || !player.onGround())
            return;

        BlockPos playerPos = player.blockPosition();
        BlockPos blockPos = new BlockPos(playerPos.getX(), playerPos.getY() - 1, playerPos.getZ());
        List<Block> diggableBlocks = new ArrayList<>();
        diggableBlocks.add(Blocks.DIRT);
        diggableBlocks.add(Blocks.GRASS_BLOCK);
        diggableBlocks.add(Blocks.PODZOL);
        diggableBlocks.add(Blocks.ROOTED_DIRT);
        diggableBlocks.add(Blocks.MOSS_BLOCK);
        diggableBlocks.add(Blocks.MUD);
        diggableBlocks.add(Blocks.MUDDY_MANGROVE_ROOTS);

        // Check if the block below the player is in the diggable blocks list
        if (diggableBlocks.contains(world.getBlockState(blockPos).getBlock()) && Math.random() <= 0.5D) {
            // drop item
            if (getRandomBoolean())
                player.spawnAtLocation(world, Items.TORCHFLOWER_SEEDS);
            else
                player.spawnAtLocation(world, Items.PITCHER_POD);

            world.playSound(null, player, SoundEvents.SNIFFER_DIGGING, SoundSource.PLAYERS, 1.0F, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 1.0F);
        } else
            world.playSound(null, player, SoundEvents.SNIFFER_DIGGING_STOP, SoundSource.PLAYERS, 1.0F, (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public Item getIcon() {
        return Items.TORCHFLOWER;
    }

    private static boolean getRandomBoolean() {
        return Math.random() < 0.5;
    }

    @Override
    public int getDefaultCooldown() {
        return 9600;
    }
}
