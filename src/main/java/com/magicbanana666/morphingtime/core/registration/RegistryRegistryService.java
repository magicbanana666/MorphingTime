package com.magicbanana666.morphingtime.core.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface RegistryRegistryService {
    <T> Registry<T> createSimpleRegistry(ResourceKey<Registry<T>> key);
}
