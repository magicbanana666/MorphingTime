package com.magicbanana666.morphingtime.core.config;

import com.magicbanana666.morphingtime.core.MorphCore;
import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import com.magicbanana666.morphingtime.core.platform.PlatformData;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@SuppressWarnings("unused")
public interface Config {
    @SuppressWarnings("SameReturnValue")
    @Nullable
    String getName();

    @Nullable
    default Path getPath() {
        return getName() != null ? ConfigLoader.getConfigPath(getName()) : null;
    }

    default void save() {
        ConfigLoader.writeConfigFile(this);
    }

    default void sendToPlayer(ServerPlayer target) {
        ModernNetworking.sendToPlayer(target, ConfigLoader.CONFIG_SYNC, ConfigLoader.getConfigSyncTag(this));
    }

    default void sendToAllPlayers(@NotNull ServerLevel serverLevel) {
        for (ServerPlayer target : serverLevel.players()) {
            sendToPlayer(target);
        }
    }

    @Nullable
    
    default Screen constructConfigScreen(Screen parent) {
        // No config screen: the settings are edited in the json5 files
        return null;
    }
}
