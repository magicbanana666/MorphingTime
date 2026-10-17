package com.magicbanana666.morphingtime.shape.impl;

import org.jetbrains.annotations.ApiStatus;

/**
 * Pass player data to the shape
 */
@ApiStatus.Internal
public interface ShapeDataProvider {

    int shape$shapedPlayer();

    void shape$ShapedPlayer(int id);
}
