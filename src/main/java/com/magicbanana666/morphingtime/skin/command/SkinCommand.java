package com.magicbanana666.morphingtime.skin.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.magicbanana666.morphingtime.core.event.common.CommandEvents;
import com.magicbanana666.morphingtime.skin.PlayerSkins;
import com.magicbanana666.morphingtime.skin.data.SkinPlayerData;
import com.magicbanana666.morphingtime.skin.permission.SkinPermissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

public class SkinCommand implements CommandEvents.CommandRegistration {

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
        onRegister(dispatcher);
    }

    private void onRegister(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("skin")
                .build();

        LiteralCommandNode<CommandSourceStack> set = buildSetCommand();
        LiteralCommandNode<CommandSourceStack> reset = buildResetCommand();
        LiteralCommandNode<CommandSourceStack> changeChatName = buildChangeChatNameCommand();
        LiteralCommandNode<CommandSourceStack> setURI = buildSetURICommand();

        rootNode.addChild(ListPermissionsCommand.createNode());
        rootNode.addChild(set);
        rootNode.addChild(reset);
        rootNode.addChild(changeChatName);
        rootNode.addChild(setURI);

        LiteralCommandNode<CommandSourceStack> morphingTime = Commands.literal(com.magicbanana666.morphingtime.MorphingTime.MODID).build();
        morphingTime.addChild(rootNode);
        dispatcher.getRoot().addChild(morphingTime);
        com.magicbanana666.morphingtime.MorphingTimeCommands.addAlias(dispatcher);
    }

    private static LiteralCommandNode<CommandSourceStack> buildSetCommand() {
        return Commands.literal("set")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("playerUUID", UuidArgument.uuid())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    ServerPlayer sender = getSenderOrNull(context);

                                    checkPermissions(context, sender, player, "set");

                                    UUID playerUUID = UuidArgument.getUuid(context, "playerUUID");
                                    PlayerSkins.setSkinURI(player, null, false);
                                    PlayerSkins.setSkin(player, playerUUID);
                                    Optional<GameProfile> profile = SkinPlayerData.getSkinProfile(
                                            context.getSource().getServer().services().profileResolver(), playerUUID);
                                    context.getSource().sendSuccess(
                                            () -> Component.translatable("skinshifter.command.set",
                                                    player.getName(),
                                                    profile.orElse(player.getGameProfile()).name()),
                                            true);
                                    return 1;
                                }))
                        .then(Commands.argument("playerName", MessageArgument.message())
                                .executes(context -> {
                                    ServerPlayer player = EntityArgument.getPlayer(context, "player");
                                    ServerPlayer sender = getSenderOrNull(context);

                                    checkPermissions(context, sender, player, "set");

                                    String playerName = MessageArgument.getMessage(context, "playerName").getString();
                                    @NotNull Optional<GameProfile> profile = SkinPlayerData.getSkinProfile(
                                            context.getSource().getServer().services().profileResolver(), playerName);
                                    if (profile.isEmpty()) {
                                        context.getSource().sendSuccess(
                                                () -> Component.translatable("skinshifter.invalid_player", playerName), true);
                                    } else {
                                        PlayerSkins.setSkin(player, profile.get().id());
                                        context.getSource().sendSuccess(
                                                () -> Component.translatable("skinshifter.command.set",
                                                        player.getName(), playerName),
                                                true);
                                    }
                                    return 1;
                                })))
                .build();
    }

    private static LiteralCommandNode<CommandSourceStack> buildResetCommand() {
        return Commands.literal("reset")
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(context -> {
                            ServerPlayer player = EntityArgument.getPlayer(context, "player");
                            ServerPlayer sender = getSenderOrNull(context);

                            checkPermissions(context, sender, player, "reset");

                            PlayerSkins.setSkin(player, null);
                            PlayerSkins.setSkinURI(player, null, false);
                            context.getSource().sendSuccess(
                                    () -> Component.translatable("skinshifter.command.reset", player.getName()), true);
                            return 1;
                        }))
                .build();
    }

    private static LiteralCommandNode<CommandSourceStack> buildChangeChatNameCommand() {
        return Commands.literal("changeChatName")
                .requires(source -> {
                    ServerPlayer player = source.getPlayer();
                    if (player != null) {
                        if (PlayerSkins.CONFIG.usePermissions) {
                            return SkinPermissions.canUseCommand(player, "changeChatName");
                        } else {
                            PermissionLevel requiredLevel = PermissionLevel.byId(PlayerSkins.CONFIG.baseCommandOPLevel);
                            return source.permissions().hasPermission(new Permission.HasCommandLevel(requiredLevel));
                        }
                    }
                    return true;
                })
                .executes(context -> {
                    boolean current = PlayerSkins.CONFIG.changeName;
                    context.getSource().sendSuccess(
                            () -> Component.translatable("craftedcore.config.get", "changeChatName", String.valueOf(current)),
                            true);
                    return 1;
                })
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean value = BoolArgumentType.getBool(context, "value");
                            PlayerSkins.CONFIG.changeName = value;
                            PlayerSkins.CONFIG.save();
                            PlayerSkins.CONFIG.sendToAllPlayers(context.getSource().getLevel());
                            context.getSource().sendSuccess(
                                    () -> Component.translatable("craftedcore.config.set", "changeChatName", String.valueOf(value)),
                                    true);
                            return 1;
                        }))
                .build();
    }

    private static LiteralCommandNode<CommandSourceStack> buildSetURICommand() {
        return Commands.literal("url")
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("slim", BoolArgumentType.bool())
                                .then(Commands.argument("uri", StringArgumentType.greedyString())
                                        .executes(SkinCommand::setByURI))))
                .build();
    }

    private static ServerPlayer getSenderOrNull(CommandContext<CommandSourceStack> context) {
        try {
            return context.getSource().getPlayerOrException();
        } catch (CommandSyntaxException e) {
            return null;
        }
    }

    private static void checkPermissions(
            CommandContext<CommandSourceStack> context,
            ServerPlayer sender,
            ServerPlayer target,
            String commandName) throws CommandSyntaxException {

        if (sender == null) return;

        if (PlayerSkins.CONFIG.usePermissions) {
            if (!SkinPermissions.canUseCommandOnTarget(sender, target, commandName)) {
                throw new SimpleCommandExceptionType(
                        Component.translatable("craftedcore.command.invalid_perms")).create();
            }
        } else {
            boolean isSelf = sender.getUUID().equals(target.getUUID());
            int requiredLevelInt = isSelf
                    ? PlayerSkins.CONFIG.selfCommandOPLevel
                    : PlayerSkins.CONFIG.baseCommandOPLevel;

            PermissionLevel requiredLevel = PermissionLevel.byId(requiredLevelInt);
            if (!context.getSource().permissions().hasPermission(new Permission.HasCommandLevel(requiredLevel))) {
                throw new SimpleCommandExceptionType(
                        Component.translatable("craftedcore.command.invalid_perms")).create();
            }
        }
    }

    private static int setByURI(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        ServerPlayer sender = getSenderOrNull(context);

        checkPermissions(context, sender, player, "uri");

        String uriStr = StringArgumentType.getString(context, "uri");
        boolean slim = BoolArgumentType.getBool(context, "slim");

        try {
            URI uri = new URI(uriStr);

            PlayerSkins.setSkin(player, null);
            PlayerSkins.setSkinURI(player, uriStr, slim);

            Component uriText = Component.literal(uriStr)
                    .withStyle(Style.EMPTY
                            .withColor(ChatFormatting.AQUA)
                            .withUnderlined(true)
                            .withClickEvent(new ClickEvent.OpenUrl(uri)));

            context.getSource().sendSuccess(
                    () -> Component.translatable("skinshifter.command.set", player.getName(), uriText),
                    true);
            return 1;
        } catch (URISyntaxException e) {
            context.getSource().sendFailure(Component.translatable("skinshifter.command.invalid_uri"));
            return 0;
        }
    }
}