package com.magicbanana666.morphingtime.collection.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import org.jetbrains.annotations.NotNull;

/**
 * Command that lists all shape and skin permission nodes for easy copy-paste into LuckPerms.
 */
public class ListPermissionsCommand {

    public static LiteralCommandNode<CommandSourceStack> createNode() {
        return Commands.literal("list-permissions")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .executes(context -> {
                    listAllPermissions(context.getSource());
                    return 1;
                }).build();
    }

    private static void listAllPermissions(@NotNull CommandSourceStack source) {
        int size = countPermissions();

        source.sendSuccess(() -> Component.literal("§6=== Morphing Time Permissions List ==="), false);
        source.sendSuccess(() -> Component.literal("§eCopy and paste these into LuckPerms:"), false);
        source.sendSuccess(() -> Component.literal(""), false);

        source.sendSuccess(() -> Component.literal("§a§lCore Permissions:"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group default permission set morphingtime.morph true"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group admin permission set morphingtime.bypass.lock true"), false);

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§a§lCommand Permissions:"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group admin permission set morphingtime.command.addshape true"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group admin permission set morphingtime.command.removeshape true"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group admin permission set morphingtime.command.clearshapes true"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group admin permission set morphingtime.command.hasshape true"), false);

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§a§lExample Entity Permissions:"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group default permission set morphingtime.type.minecraft.zombie true"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group default permission set morphingtime.type.minecraft.skeleton true"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group default permission set morphingtime.type.minecraft.creeper true"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group vip permission set morphingtime.type.minecraft.enderman true"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group admin permission set morphingtime.type.minecraft.ender_dragon true"), false);

        source.sendSuccess(() -> Component.literal(""), false);

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§a§lWildcard Permissions:"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group admin permission set morphingtime.* true §7# All permissions"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group default permission set morphingtime.type.* true §7# All entity types"), false);
        source.sendSuccess(() -> Component.literal("§f/lp group admin permission set morphingtime.command.* true §7# All commands"), false);

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§e§lTotal: " + size + " permissions available"), false);
        source.sendSuccess(() -> Component.literal("§7Use these exact commands in your server console or LuckPerms web editor"), false);
    }

    private static int countPermissions() {
        int i = 0;

        // Core permissions
        i++;   // morphingtime.morph
        i++;   // morphingtime.bypass.lock

        // Command permissions
        i++;   // morphingtime.command.addshape
        i++;   // morphingtime.command.removeshape
        i++;   // morphingtime.command.clearshapes
        i++;   // morphingtime.command.hasshape
        i++;   // morphingtime.command.addskin
        i++;   // morphingtime.command.removeskin
        i++;   // morphingtime.command.clearskins
        i++;   // morphingtime.command.hasskin

        // Entity type permissions for all registered entities
        i += BuiltInRegistries.ENTITY_TYPE.size();

        return i;
    }
}
