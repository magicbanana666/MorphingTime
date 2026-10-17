package com.magicbanana666.morphingtime.core.event.client;

import com.magicbanana666.morphingtime.core.event.Event;
import com.magicbanana666.morphingtime.core.event.EventFactory;
import net.minecraft.client.player.LocalPlayer;

@SuppressWarnings("unused")

public final class ClientPlayerEvents {
    public static final Event<ClientPlayerJoin> CLIENT_PLAYER_JOIN = EventFactory.createWithVoid();
    public static final Event<ClientPlayerQuit> CLIENT_PLAYER_QUIT = EventFactory.createWithVoid();
    /**
     * Called when the player is recreated
     */
    public static final Event<ClientPlayerRespawn> CLIENT_PLAYER_RESPAWN = EventFactory.createWithVoid();

    
    @FunctionalInterface
    public interface ClientPlayerJoin {
        void join(LocalPlayer player);
    }

    
    @FunctionalInterface
    public interface ClientPlayerQuit {
        void quit(LocalPlayer player);
    }

    
    @FunctionalInterface
    public interface ClientPlayerRespawn {
        void respawn(LocalPlayer oldPlayer, LocalPlayer newPlayer);
    }
}
