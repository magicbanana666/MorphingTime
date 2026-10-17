package com.magicbanana666.morphingtime.shape.api;

import com.magicbanana666.morphingtime.shape.impl.tick.shapes.FrogTickHandler;
import com.magicbanana666.morphingtime.shape.impl.tick.shapes.JumpBoostTickHandler;
import com.magicbanana666.morphingtime.shape.impl.tick.shapes.SnowGolemTickHandler;
import com.magicbanana666.morphingtime.shape.impl.tick.shapes.WardenTickHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public class ShapeTickHandlers {

    private static final Map<EntityType<?>, ShapeTickHandler<?>> HANDLERS = new LinkedHashMap<>();

    @ApiStatus.Internal
    public static void initialize() {
        register(EntityType.WARDEN, new WardenTickHandler());
        register(EntityType.FROG, new FrogTickHandler());
        register(EntityType.CAMEL, new JumpBoostTickHandler<>(0));
        register(EntityType.SNOW_GOLEM, new SnowGolemTickHandler());
        register(EntityType.RABBIT, new JumpBoostTickHandler<>(1));
        register(EntityType.GOAT, new JumpBoostTickHandler<>(0)); // raise the default goat jump height
        register(EntityType.MAGMA_CUBE, new JumpBoostTickHandler<>(2));
    }

    public static <T extends LivingEntity> void register(EntityType<T> type, ShapeTickHandler<T> handler) {
        HANDLERS.put(type, handler);
    }

    @ApiStatus.Internal
    public static Map<EntityType<?>, ShapeTickHandler<?>> getHandlers() {
        return HANDLERS;
    }
}
