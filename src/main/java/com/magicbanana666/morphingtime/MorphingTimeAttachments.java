package com.magicbanana666.morphingtime;

import java.util.function.Supplier;

import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** Data attachments used instead of synced entity data, which cannot be added to vanilla entities. */
public final class MorphingTimeAttachments {

    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, MorphingTime.MODID);

    /** Whether a wolf uses the special wolf look. Saved by the wolf itself as "isSpecial" and synced to all clients. */
    public static final Supplier<AttachmentType<Boolean>> SPECIAL_WOLF = REGISTER.register("special_wolf",
            () -> AttachmentType.builder(() -> false).sync(ByteBufCodecs.BOOL).build());

    private MorphingTimeAttachments() {
    }
}
