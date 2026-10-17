package com.magicbanana666.morphingtime.core.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.ServiceLoader;

public class RegistryRegistry {
    private static final RegistryRegistryService SERVICE =
            ServiceLoader.load(RegistryRegistryService.class).findFirst().orElseThrow();

    public static <T> Registry<T> createSimpleRegistry(ResourceKey<Registry<T>> key) {
        return SERVICE.createSimpleRegistry(key);
    }
}
