package com.magicbanana666.morphingtime.core.platform.fabric;

import com.magicbanana666.morphingtime.core.platform.PlatformData;
import com.magicbanana666.morphingtime.core.platform.PlatformDataService;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.module.ModuleDescriptor.Version;
import java.nio.file.Path;

@SuppressWarnings({"unused", "SameReturnValue"})
@ApiStatus.Internal
public final class PlatformDataImpl implements PlatformDataService {
    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    @Nullable
    public Version getModVersion(String modid) {
        return FabricLoader.getInstance().getModContainer(modid).map(modContainer -> {
            try {
                return Version.parse(modContainer.getMetadata().getVersion().getFriendlyString());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }).orElse(null);
    }

    @Override
    public boolean isDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public PlatformData.Env getEnv() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? PlatformData.Env.CLIENT : PlatformData.Env.SERVER;
    }

    @Override
    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public PlatformData.ModLoader getModLoaderId() {
        return PlatformData.ModLoader.FABRIC;
    }

    @Override
    @ApiStatus.Internal
    @SuppressWarnings("EmptyMethod")
    public void registerConfigScreen(String name) {
        // No config screens: the settings are edited in the json5 files (on NeoForge the screen is only used with Cloth Config)
    }
}
