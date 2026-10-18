package com.magicbanana666.morphingtime.core.permission.fabric;

import com.magicbanana666.morphingtime.core.permission.PermissionCheckerService;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionLevel;
import org.jetbrains.annotations.NotNull;

/**
 * Checks permissions through the Fabric Permission API, so permission mods can grant them.
 * Without a permission mod, a permission is granted to operators (permission level 2, like the gamemaster commands).
 */
@SuppressWarnings("unused")
public class PermissionCheckerImpl implements PermissionCheckerService {

    /**
     * Converts a permission name such as "command.addShape" or "type.minecraft:pig" into a valid
     * identifier, since permission nodes must be identifiers.
     */
    private static Identifier nodeId(String namespace, String permission) {
        StringBuilder path = new StringBuilder();
        for (char c : permission.toLowerCase(java.util.Locale.ROOT).toCharArray()) {
            path.append(Identifier.isAllowedInIdentifier(c) && c != ':' ? c : (c == '*' ? "all" : "."));
        }
        return Identifier.fromNamespaceAndPath(namespace, path.toString());
    }

    @Override
    public boolean hasPermission(@NotNull ServerPlayer player, @NotNull String namespace, @NotNull String permission) {
        return player.checkPermission(nodeId(namespace, permission), PermissionLevel.GAMEMASTERS);
    }
}
