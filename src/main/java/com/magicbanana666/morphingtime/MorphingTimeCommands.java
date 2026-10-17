package com.magicbanana666.morphingtime;

import java.util.function.Predicate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * All mod commands are registered under {@code /morphingtime}, with {@code /morph} as a short alias.
 */
public final class MorphingTimeCommands {

    private static final Predicate<CommandSourceStack> OPERATOR = Commands.hasPermission(Commands.LEVEL_GAMEMASTERS);

    private MorphingTimeCommands() {
    }

    /** Copies a command node and restricts it to operators, on top of its existing requirement. */
    public static LiteralCommandNode<CommandSourceStack> operatorOnly(LiteralCommandNode<CommandSourceStack> node) {
        final Predicate<CommandSourceStack> requirement = node.getRequirement();
        final LiteralCommandNode<CommandSourceStack> copy = new LiteralCommandNode<>(node.getLiteral(), node.getCommand(),
                source -> OPERATOR.test(source) && requirement.test(source), node.getRedirect(), node.getRedirectModifier(), node.isFork());
        for (final CommandNode<CommandSourceStack> child : node.getChildren()) {
            copy.addChild(child);
        }
        return copy;
    }

    /** Adds {@code /morph} as a short version of {@code /morphingtime}. */
    public static void addAlias(CommandDispatcher<CommandSourceStack> dispatcher) {
        final CommandNode<CommandSourceStack> root = dispatcher.getRoot().getChild(MorphingTime.MODID);
        if (root != null && dispatcher.getRoot().getChild("morph") == null) {
            dispatcher.register(Commands.literal("morph").redirect(root));
        }
    }
}
