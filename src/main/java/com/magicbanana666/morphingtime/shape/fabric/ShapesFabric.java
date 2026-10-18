package com.magicbanana666.morphingtime.shape.fabric;

import com.magicbanana666.morphingtime.core.platform.PlatformData;
import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.ShapesClient;

@SuppressWarnings("unused")
// Created by the main mod class; initializes the common side and, on the client, the client side
public class ShapesFabric {
    public ShapesFabric() {
        new Shapes().initialize();

        if (PlatformData.getEnv().isClient()) {
            new ShapesClient().initialize();
        }
    }
}
