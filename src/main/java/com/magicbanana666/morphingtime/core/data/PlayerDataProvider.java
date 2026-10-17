package com.magicbanana666.morphingtime.core.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

@ApiStatus.Internal
public interface PlayerDataProvider {
    <O> void core$writeTag(String key, @Nullable O value);

    Set<String> core$keySet();

    @Nullable <T> T core$readTag(String key, Class<T> type);

    @Nullable Object core$readTag(String key);
}