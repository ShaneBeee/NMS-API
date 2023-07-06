package com.shanebeestudios.nms.api.reflection;

import org.bukkit.Bukkit;

public class ReflectionConstants {

    // net.minecraft.world.level.chunk.LevelChunk -> isTicking(BlockPos)
    public static String LEVEL_CHUNK_IS_TICKING_METHOD = get("k");

    @SuppressWarnings("SameParameterValue")
    private static String get(String v1194) {
        if (ReflectionUtils.isRunningMinecraft(1,19,4)) {
            return v1194;
        }
        throw new IllegalArgumentException("Unknown Version: " + Bukkit.getBukkitVersion());
    }

}
