package com.magicbanana666.morphingtime.collection.network;

import com.magicbanana666.morphingtime.core.network.ModernNetworking;
import net.minecraft.nbt.CompoundTag;

/**
 * Network packet for checking permissions - client-side methods only
 */
@SuppressWarnings("unused")
public class PermissionCheckPacket {

    /**
     * Send a permission check request to the server
     *
     * @param permission The permission to check
     */
    
    public static void sendPermissionCheck(String permission) {
        CompoundTag tag = new CompoundTag();
        tag.putString("permission", permission);
        ModernNetworking.sendToServer(NetworkHandler.PERMISSION_CHECK, tag);
    }

    /**
     * Handle permission check response on client side
     *
     * @param context The network context
     * @param tag     The response data
     */
    
    public static void handlePermissionResponse(ModernNetworking.Context context, CompoundTag tag) {
        String permission = tag.getString("permission").orElse("");
        boolean hasPermission = tag.getBoolean("hasPermission").orElse(false);

        // Store the permission result for client-side use
        ClientPermissionCache.setPermission(permission, hasPermission);

    }
}
