package com.magicbanana666.morphingtime.core.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.magicbanana666.morphingtime.core.event.common.PlayerEvents;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@SuppressWarnings({"DataFlowIssue", "unused"})
@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    // The time the players wake up at after sleeping
    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/clock/ServerClockManager;moveToTimeMarker(Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceKey;)Z"
            )
    )
    private boolean sleepFinishedTime(ServerClockManager clockManager, Holder<WorldClock> clock, ResourceKey<ClockTimeMarker> marker, Operation<Boolean> original) {
        ServerLevel level = (ServerLevel) (Object) this;
        Optional<Long> newTime = PlayerEvents.SLEEP_FINISHED_TIME.invoke().setWakeUpTime(level, level.getOverworldClockTime(), -1);
        if (newTime.isPresent()) {
            clockManager.setTotalTicks(clock, newTime.get());
            return true;
        }
        return original.call(clockManager, clock, marker);
    }
}
