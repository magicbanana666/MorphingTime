package com.magicbanana666.morphingtime.core.mixin.client;

import com.magicbanana666.morphingtime.core.util.TraceUtils;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

// based on code by comp500 (licensed as MIT, take a look https://github.com/comp500/mixintrace for details)

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Inject(method = "crash", at = @At("HEAD"))
    private static void onCrash(Minecraft minecraft, File gameDirectory, @NotNull CrashReport report, int exitCode, CallbackInfo ci) {
        StringBuilder crashReportBuilder = new StringBuilder();
        TraceUtils.printMixinTrace(report.getException().getStackTrace(), crashReportBuilder);
        System.out.println(crashReportBuilder);
    }
}
