package com.magicbanana666.morphingtime.core.event.client;

import com.magicbanana666.morphingtime.core.event.Event;
import com.magicbanana666.morphingtime.core.event.EventFactory;
import net.minecraft.client.Minecraft;

@SuppressWarnings("unused")

public final class ClientTickEvents {
    public static final Event<Client> CLIENT_PRE = EventFactory.createWithVoid();
    public static final Event<Client> CLIENT_POST = EventFactory.createWithVoid();

    
    @FunctionalInterface
    public interface Client {
        void tick(Minecraft instance);
    }
}
