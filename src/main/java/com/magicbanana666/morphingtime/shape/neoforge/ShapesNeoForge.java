package com.magicbanana666.morphingtime.shape.neoforge;

import com.magicbanana666.morphingtime.shape.Shapes;
import com.magicbanana666.morphingtime.shape.ShapesClient;
import net.neoforged.fml.loading.FMLEnvironment;

@SuppressWarnings("unused")
// Created by the main mod class; initializes the common side and, on the client, the client side
public class ShapesNeoForge {
    public ShapesNeoForge() {
        new Shapes().initialize();

        if (FMLEnvironment.getDist().isClient()) {
            new ShapesClient().initialize();
        }
    }
}
