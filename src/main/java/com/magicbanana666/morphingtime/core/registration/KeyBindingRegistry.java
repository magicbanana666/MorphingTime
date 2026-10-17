package com.magicbanana666.morphingtime.core.registration;

import net.minecraft.client.KeyMapping;

import java.util.ServiceLoader;

@SuppressWarnings("unused")

public final class KeyBindingRegistry {
    private static final KeyBindingRegistryService SERVICE =
            ServiceLoader.load(KeyBindingRegistryService.class).findFirst().orElseThrow();

    public static void register(KeyMapping keyMapping) {
        SERVICE.register(keyMapping);
    }
}
