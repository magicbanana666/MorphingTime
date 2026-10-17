package com.magicbanana666.morphingtime.shape.impl;

import com.magicbanana666.morphingtime.shape.mixin.player.PlayerEntityMixin;

/**
 * Duck interface for accessing information about nearby playing music in {@link PlayerEntityMixin}.
 */
public interface NearbySongAccessor {
    boolean shape_isNearbySongPlaying();
}
