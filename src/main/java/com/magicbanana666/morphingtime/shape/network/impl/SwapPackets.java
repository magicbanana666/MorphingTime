package com.magicbanana666.morphingtime.shape.network.impl;

import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.api.PlayerShape;
import com.magicbanana666.morphingtime.shape.api.platform.ApiLevel;
import com.magicbanana666.morphingtime.shape.api.variant.ShapeType;
import com.magicbanana666.morphingtime.shape.impl.PlayerDataProvider;
import com.magicbanana666.morphingtime.shape.network.ClientNetworking;
import com.magicbanana666.morphingtime.shape.network.NetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class SwapPackets {

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public static void registerWalkersRequestPacketHandler() {
        ModernNetworking.registerReceiver(ModernNetworking.Side.C2S, NetworkHandler.SHAPE_REQUEST,
                (context, packet) -> ((net.minecraft.server.level.ServerLevel) context.getPlayer().level()).getServer().execute(() -> {
                    // check if player is blacklisted
                    if (Shapes.isPlayerBlacklisted(context.getPlayer().getUUID()) && Shapes.CONFIG.blacklistPreventsMorphing) {
                        context.getPlayer().sendOverlayMessage(Component.translatable("walkers.player_blacklisted"));
                        return;
                    }

                    if (!ApiLevel.getCurrentLevel().canMorph) {
                        return;
                    }

                    // default the ShapeType to null so an invalid second shape can't cause a crash
                    @Nullable
                    ShapeType<LivingEntity> type = null;
                    // Get the player's second shape if the player is currently in their normal form
                    if (PlayerShape.getCurrentShape(context.getPlayer()) == null) {
                        type = (@Nullable ShapeType<LivingEntity>) ((PlayerDataProvider) context.getPlayer()).shape$get2ndShape();
                    }

                    // Swap to the other shape
                    if (type != null) {
                        // update player
                        PlayerShape.updateShapes((ServerPlayer) context.getPlayer(),
                                type.create(context.getPlayer().level(), context.getPlayer()));
                    } else {
                        // Swap back to player if server allows it
                        PlayerShape.updateShapes((ServerPlayer) context.getPlayer(), null);
                    }

                    // Refresh player dimensions
                    context.getPlayer().refreshDimensions();
                }));
    }

    public static void sendSwapRequest() {
        if (!ApiLevel.getCurrentLevel().canMorph) {
            return;
        }

        ModernNetworking.sendToServer(ClientNetworking.SHAPE_REQUEST, new CompoundTag());
    }
}
