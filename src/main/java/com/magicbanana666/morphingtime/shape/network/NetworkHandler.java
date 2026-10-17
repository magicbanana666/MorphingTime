package com.magicbanana666.morphingtime.shape.network;

import com.magicbanana666.morphingtime.shape.Shapes;
import net.minecraft.resources.Identifier;

public interface NetworkHandler {
    Identifier SHAPE_REQUEST = Shapes.id("request");
    Identifier UNLOCK_REQUEST = Shapes.id("unlock_request");
    Identifier USE_ABILITY = Shapes.id("use_ability");
    Identifier SHAPE_SYNC = Shapes.id("shape_sync");
    Identifier ABILITY_SYNC = Shapes.id("ability_sync");
    Identifier UNLOCK_SYNC = Shapes.id("unlock_sync");
    Identifier VARIANT_REQUEST = Shapes.id("variant");
    Identifier SYNC_API_LEVEL = Shapes.id("sync_api_level");
}
