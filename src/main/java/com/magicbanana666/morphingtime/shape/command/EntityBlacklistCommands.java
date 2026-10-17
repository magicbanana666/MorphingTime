package com.magicbanana666.morphingtime.shape.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.magicbanana666.morphingtime.shape.Shapes;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class EntityBlacklistCommands {
    public static LiteralCommandNode<CommandSourceStack> getRootNode(CommandBuildContext ctx) {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("entityBlacklist").build();

        LiteralCommandNode<CommandSourceStack> addToList = Commands.literal("add")
                .then(Commands.argument("entity", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.cast(SuggestionProviders.SUMMONABLE_ENTITIES))
                        .executes(context -> {
                            addToList(context.getSource(), ResourceArgument.getSummonableEntityType(context, "entity").key().identifier());
                            return 1;
                        }))
                .build();
        LiteralCommandNode<CommandSourceStack> removeFromList = Commands.literal("remove")
                .then(Commands.argument("entity", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.cast(SuggestionProviders.SUMMONABLE_ENTITIES))
                        .executes(context -> {
                            removeFromList(context.getSource(), ResourceArgument.getSummonableEntityType(context, "entity").key().identifier());
                            return 1;
                        }))
                .build();

        LiteralCommandNode<CommandSourceStack> listList = Commands.literal("list")
                .executes(context -> listEntities(context.getSource()))
                .build();

        LiteralCommandNode<CommandSourceStack> clearList = Commands.literal("clear")
                .executes(context -> clearEntities(context.getSource()))
                .build();


        LiteralCommandNode<CommandSourceStack> isWhitelist = Commands.literal("isWhitelist")
                .executes(context -> {
                    isWhitelist(context.getSource());
                    return 1;
                })
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> {
                            setIsWhitelist(context.getSource(), BoolArgumentType.getBool(context, "value"));
                            return 1;
                        }))
                .build();

        rootNode.addChild(listList);
        rootNode.addChild(clearList);
        rootNode.addChild(addToList);
        rootNode.addChild(removeFromList);
        rootNode.addChild(isWhitelist);
        return rootNode;
    }

    private static void isWhitelist(CommandSourceStack source) {
        source.sendSuccess(() -> Component.translatable("craftedcore.config.get", "entityBlacklistIsWhitelist", Shapes.CONFIG.entityBlacklistIsWhitelist), false);
    }

    private static void setIsWhitelist(CommandSourceStack source, boolean value) {
        Shapes.CONFIG.entityBlacklistIsWhitelist = value;
        Shapes.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Shapes.CONFIG.sendToPlayer(player);
        }

        source.sendSuccess(() -> Component.translatable("craftedcore.config.set", "entityBlacklistIsWhitelist", String.valueOf(value)), false);
    }

    private static int clearEntities(CommandSourceStack source) {
        Shapes.CONFIG.entityBlacklist.clear();
        Shapes.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Shapes.CONFIG.sendToPlayer(player);
        }

        source.sendSuccess(() -> Component.translatable("walkers.entityBlacklist.clear"), false);

        return 1;
    }

    private static int listEntities(CommandSourceStack source) {
        for (String s : Shapes.CONFIG.entityBlacklist) {
            source.sendSuccess(() -> Component.translatable("walkers.entityBlacklist.list", s), false);
        }

        if (Shapes.CONFIG.entityBlacklist.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("walkers.entityBlacklist.isEmpty"), false);
        }

        return 1;
    }

    private static void addToList(CommandSourceStack source, Identifier type) {
        Shapes.CONFIG.entityBlacklist.add(type.toString());
        Shapes.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Shapes.CONFIG.sendToPlayer(player);
        }

        source.sendSuccess(() -> Component.translatable("walkers.entityBlacklist.add", type.toString()), false);
    }

    private static void removeFromList(CommandSourceStack source, Identifier type) {
        Shapes.CONFIG.entityBlacklist.remove(type.toString());
        Shapes.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Shapes.CONFIG.sendToPlayer(player);
        }

        source.sendSuccess(() -> Component.translatable("walkers.entityBlacklist.remove", type.toString()), false);
    }
}
