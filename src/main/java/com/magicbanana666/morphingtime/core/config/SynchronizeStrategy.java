package com.magicbanana666.morphingtime.core.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.magicbanana666.morphingtime.core.config.annotions.Synchronize;
import org.jetbrains.annotations.NotNull;

public class SynchronizeStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(@NotNull FieldAttributes f) {
        return f.getAnnotations().stream().noneMatch(annotation -> annotation instanceof Synchronize);
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
