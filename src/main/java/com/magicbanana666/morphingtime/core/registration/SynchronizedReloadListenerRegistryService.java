package com.magicbanana666.morphingtime.core.registration;

import com.magicbanana666.morphingtime.core.data.SynchronizedJsonReloadListener;
import net.minecraft.resources.Identifier;

public interface SynchronizedReloadListenerRegistryService {
    void onRegister(SynchronizedJsonReloadListener reloadListener, Identifier id);
}
