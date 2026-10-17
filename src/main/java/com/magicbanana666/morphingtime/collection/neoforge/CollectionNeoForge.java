package com.magicbanana666.morphingtime.collection.neoforge;

import com.magicbanana666.morphingtime.core.permission.neoforge.PermissionCheckerImpl;
import com.magicbanana666.morphingtime.collection.MorphCollection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class CollectionNeoForge {
    public CollectionNeoForge() {
        NeoForge.EVENT_BUS.addListener(PermissionGatherEvent.Nodes.class, CollectionNeoForge::registerNodesEvent);
        new MorphCollection().initialize();
    }

    public static void registerNodesEvent(PermissionGatherEvent.@NotNull Nodes event) {
        List<PermissionNode<@NotNull Boolean>> nodes = new ArrayList<>();

        // Core permissions
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "morph"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "bypass.lock"));

        // Command permissions (basic)
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.addShape"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.removeShape"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.clearShapes"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.hasShape"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.addSkin"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.removeSkin"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.clearSkins"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.hasSkin"));

        // Command permissions (.self variants)
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.addShape.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.removeShape.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.clearShapes.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.hasShape.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.addSkin.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.removeSkin.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.clearSkins.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.hasSkin.self"));

        // Command permissions (.others variants)
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.addShape.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.removeShape.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.clearShapes.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.hasShape.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.addSkin.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.removeSkin.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.clearSkins.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "command.hasSkin.others"));

        // Entity type permissions for all registered entities
        BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
            Identifier key = EntityType.getKey(entityType);
            nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "type." + key));
        });

        // Register all nodes
        event.addNodes(nodes.toArray(PermissionNode[]::new));
    }
}
