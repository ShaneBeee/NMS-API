package com.shanebeestudios.nms.api.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;

public class RegistryUtils {

    /**
     * Get a Minecraft Registry
     *
     * @param registry ResourceKey of registry
     * @param <T>      ResourceKey
     * @return Registry from key
     */
    @SuppressWarnings("deprecation")
    public static <T> Registry<T> getRegistry(ResourceKey<? extends Registry<? extends T>> registry) {
        return MinecraftServer.getServer().registryAccess().registryOrThrow(registry);
    }

}
