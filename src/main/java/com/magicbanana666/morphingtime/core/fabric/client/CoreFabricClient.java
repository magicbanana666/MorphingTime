package com.magicbanana666.morphingtime.core.fabric.client;

import com.magicbanana666.morphingtime.core.client.MorphCoreClient;


public class CoreFabricClient {
    public CoreFabricClient() {
        CoreFabricEventHandlerClient.register();

        new MorphCoreClient().initialize();
    }
}
