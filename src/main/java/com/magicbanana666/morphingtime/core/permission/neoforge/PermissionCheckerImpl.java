package com.magicbanana666.morphingtime.core.permission.neoforge;

import com.magicbanana666.morphingtime.core.permission.PermissionCheckerService;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unused")
public class PermissionCheckerImpl implements PermissionCheckerService {
    private static final ConcurrentMap<Identifier, PermissionNode<Boolean>> PERMISSION_NODES = new ConcurrentHashMap<>();

    /**
     * Converts a permission name such as "command.addShape" or "type.minecraft:pig" into a valid
     * identifier, since permission nodes must be identifiers. Used for both registration and lookup.
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
        Identifier id = nodeId(namespace, permission);
        PermissionNode<Boolean> node = PERMISSION_NODES.get(id);
        return node != null ? PermissionAPI.getPermission(player, node) : player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);
    }

    public static PermissionNode<Boolean> createNode(@NotNull String namespace, @NotNull String permission) {
        Identifier id = nodeId(namespace, permission);
        // Return the new node itself, Map.put would return the previous value (null)
        PermissionNode<Boolean> node = new PermissionNode<>(id, PermissionTypes.BOOLEAN,
                (player, playerUUID, context) -> player != null && player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER));
        PERMISSION_NODES.put(id, node);
        return node;
    }
}
