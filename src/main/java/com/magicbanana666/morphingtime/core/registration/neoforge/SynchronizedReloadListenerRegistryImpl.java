package com.magicbanana666.morphingtime.core.registration.neoforge;

import com.magicbanana666.morphingtime.core.data.SynchronizedJsonReloadListener;
import com.magicbanana666.morphingtime.core.registration.SynchronizedReloadListenerRegistryService;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
@ApiStatus.Internal
public class SynchronizedReloadListenerRegistryImpl implements SynchronizedReloadListenerRegistryService {
    @Override
    @SuppressWarnings("EmptyMethod")
    public void onRegister(SynchronizedJsonReloadListener reloadListener, Identifier id) {
    }
}
