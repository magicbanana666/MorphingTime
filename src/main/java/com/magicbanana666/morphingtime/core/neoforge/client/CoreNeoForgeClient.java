package com.magicbanana666.morphingtime.core.neoforge.client;

import com.magicbanana666.morphingtime.core.client.MorphCoreClient;
import com.magicbanana666.morphingtime.core.registration.neoforge.KeyBindingRegistryImpl;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;


public class CoreNeoForgeClient {
    public CoreNeoForgeClient(IEventBus bus) {
        NeoForge.EVENT_BUS.register(new CoreNeoForgeEventHandlerClient());
        bus.addListener(KeyBindingRegistryImpl::event);

        new MorphCoreClient().initialize();
    }
}
