package com.magicbanana666.morphingtime.skin.neoforge;

import com.magicbanana666.morphingtime.core.permission.neoforge.PermissionCheckerImpl;
import com.magicbanana666.morphingtime.skin.PlayerSkins;
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
public class SkinNeoForge {
    public SkinNeoForge() {
        NeoForge.EVENT_BUS.addListener(PermissionGatherEvent.Nodes.class, SkinNeoForge::registerNodesEvent);
        new PlayerSkins().initialize();
    }

    public static void registerNodesEvent(PermissionGatherEvent.@NotNull Nodes event) {
        List<PermissionNode<Boolean>> nodes = new ArrayList<>();

        // Command permissions (basic)
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.set"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.uri"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.reset"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.changeChatName"));

        // Command permissions (.self variants)
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.set.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.uri.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.reset.self"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.changeChatName.self"));

        // Command permissions (.others variants)
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.set.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.uri.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.reset.others"));
        nodes.add(PermissionCheckerImpl.createNode(com.magicbanana666.morphingtime.MorphingTime.MODID, "skin.command.changeChatName.others"));

        // Entity type permissions for all registered entities
        BuiltInRegistries.ENTITY_TYPE.forEach(entityType -> {
            Identifier key = EntityType.getKey(entityType);
            nodes.add(PermissionCheckerImpl.createNode("skinshifter", "type." + key));
        });


        // Register all nodes
        event.addNodes(nodes.toArray(PermissionNode[]::new));
    }
}
