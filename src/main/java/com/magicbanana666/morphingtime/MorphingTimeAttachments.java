package com.magicbanana666.morphingtime;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

/** Data attachments used instead of synced entity data, which cannot be added to vanilla entities. */
public final class MorphingTimeAttachments {

    /** Whether a wolf uses the special wolf look. Saved by the wolf itself as "isSpecial" and synced to all clients. */
    public static final AttachmentType<Boolean> SPECIAL_WOLF = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(MorphingTime.MODID, "special_wolf"),
            builder -> builder.initializer(() -> false).syncWith(ByteBufCodecs.BOOL, AttachmentSyncPredicate.all()));

    /** The UUID of the player behind a shape entity, so other mods can find who is behind it. Not saved or synced. */
    public static final AttachmentType<String> SHAPE_PLAYER = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(MorphingTime.MODID, "shape_player"));

    private MorphingTimeAttachments() {
    }

    /** Loads the class, which registers the attachments. */
    public static void init() {
    }
}
