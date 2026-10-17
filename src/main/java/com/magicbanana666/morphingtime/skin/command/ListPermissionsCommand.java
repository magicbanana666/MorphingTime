package com.magicbanana666.morphingtime.skin.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

/**
 * Command that lists all skin permission nodes for easy copy-paste into LuckPerms.
 */
public class ListPermissionsCommand {

    public static LiteralCommandNode<CommandSourceStack> createNode() {
        return Commands.literal("list-permissions")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .executes(context -> {
                    listAllPermissions(context.getSource());
                    return 1;
                }).build();
    }

    private static void listAllPermissions(@NotNull CommandSourceStack source) {
        int size = countPermissions();

        source.sendSuccess(() -> Component.literal("§6=== Morphing Time Skin Permissions ==="), false);
        source.sendSuccess(() -> Component.literal("§eClick on the commands to run them:"), false);
        source.sendSuccess(() -> Component.literal(""), false);

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§a§lCommand Permissions:"), false);

        // Command Permissions
        sendClickableCommand(source, "morphingtime.skin.command.set", "/lp group admin permission set morphingtime.skin.command.set true");
        sendClickableCommand(source, "morphingtime.skin.command.set.self", "/lp group admin permission set morphingtime.skin.command.set.self true");
        sendClickableCommand(source, "morphingtime.skin.command.set.others", "/lp group admin permission set morphingtime.skin.command.set.others true");
        sendClickableCommand(source, "morphingtime.skin.command.uri", "/lp group admin permission set morphingtime.skin.command.uri true");
        sendClickableCommand(source, "morphingtime.skin.command.uri.self", "/lp group admin permission set morphingtime.skin.command.uri.self true");
        sendClickableCommand(source, "morphingtime.skin.command.uri.others", "/lp group admin permission set morphingtime.skin.command.uri.others true");
        sendClickableCommand(source, "morphingtime.skin.command.reset", "/lp group admin permission set morphingtime.skin.command.reset true");
        sendClickableCommand(source, "morphingtime.skin.command.reset.self", "/lp group admin permission set morphingtime.skin.command.reset.self true");
        sendClickableCommand(source, "morphingtime.skin.command.reset.others", "/lp group admin permission set morphingtime.skin.command.reset.others true");
        sendClickableCommand(source, "morphingtime.skin.command.playername", "/lp group admin permission set morphingtime.skin.command.playername true");

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§a§lWildcard Permissions:"), false);

        // Wildcard Permissions
        sendClickableCommand(source, "morphingtime.skin.* §7# All permissions", "/lp group admin permission set morphingtime.skin.* true");
        sendClickableCommand(source, "morphingtime.skin.command.* §7# All commands", "/lp group admin permission set morphingtime.skin.command.* true");

        source.sendSuccess(() -> Component.literal(""), false);
        source.sendSuccess(() -> Component.literal("§e§lTotal: " + size + " permissions available"), false);
        source.sendSuccess(() -> Component.literal("§7Note: These commands must be run in the **server console** or a LuckPerms web editor."), false);
    }

    /**
     * Helper method to create and send a clickable chat component.
     * The command will be executed when the component is clicked.
     * The displayed text is set to white (§f).
     */
    private static void sendClickableCommand(@NotNull CommandSourceStack source, String displayPermission, String command) {
        // Create a style object with a ClickEvent
        Style clickableStyle = Style.EMPTY.withClickEvent(new ClickEvent.RunCommand(command));

        // Create the component with the desired text and apply the style
        Component clickableComponent = Component.literal("§f" + command + (displayPermission.contains("#") ? (" " + displayPermission.substring(displayPermission.indexOf("#"))) : ""))
                .withStyle(clickableStyle);

        source.sendSuccess(() -> clickableComponent, false);
    }

    private static int countPermissions() {
        int i = 0;

        // Command permissions
        i++;   // morphingtime.skin.command.set
        i++;   // morphingtime.skin.command.set.self
        i++;   // morphingtime.skin.command.set.others
        i++;   // morphingtime.skin.command.uri
        i++;   // morphingtime.skin.command.uri.self
        i++;   // morphingtime.skin.command.uri.others
        i++;   // morphingtime.skin.command.reset
        i++;   // morphingtime.skin.command.reset.self
        i++;   // morphingtime.skin.command.reset.others
        i++;   // morphingtime.skin.command.playername

        return i;
    }
}