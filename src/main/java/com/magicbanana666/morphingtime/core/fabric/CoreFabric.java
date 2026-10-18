package com.magicbanana666.morphingtime.core.fabric;

import com.magicbanana666.morphingtime.core.MorphCore;
import com.magicbanana666.morphingtime.core.fabric.client.CoreFabricClient;
import com.magicbanana666.morphingtime.core.platform.PlatformData;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
@ApiStatus.Internal
// Created by com.magicbanana666.morphingtime.MorphingTime in onInitialize
public class CoreFabric {

    public CoreFabric() {
        if (PlatformData.getEnv().isClient()) {
            new CoreFabricClient();
        }
        new MorphCore().initialize();

        CoreFabricEventHandler.register();
    }
}
