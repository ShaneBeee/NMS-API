package com.shanebeestudios.nms.api.util;

import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

/**
 * Utility class for changing Minecraft to/from Bukkit classes
 */
public class McUtils {

    /**
     * Get a Minecraft BlockPos from a Bukkit Location
     *
     * @param location Location to change to BlockPos
     * @return BlockPos from Location
     */
    public static BlockPos getPos(Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Get a Bukkit Location from a Minecraft BlockPos and Level
     *
     * @param blockPos BlockPos to change to location
     * @param level    Level to add to location
     * @return Location from BlockPos/Level
     */
    public static Location getLocation(BlockPos blockPos, Level level) {
        return new Location(level.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    /**
     * Convert a Bukkit NamespacedKey to Minecraft ResourceLocation
     *
     * @param bukkitKey NamespacedKey to change to ResourceLocation
     * @return ResourceLocation from NamespacedKey
     */
    public static ResourceLocation getResourceLocation(NamespacedKey bukkitKey) {
        return new ResourceLocation(bukkitKey.getNamespace(), bukkitKey.getKey());
    }

    /**
     * Convert Minecraft ResourceLocation to Bukkit NamespacedKey
     *
     * @param resourceLocation ResourceLocation to change to NamespacedKey
     * @return ResourceLocation from NamespacedKey
     */
    @SuppressWarnings("deprecation")
    public static NamespacedKey getNamespacedKey(ResourceLocation resourceLocation) {
        return new NamespacedKey(resourceLocation.getNamespace(), resourceLocation.getPath());
    }

    /**
     * Get an instance of ServerLevel from a {@link World Bukkit World}
     *
     * @param world World to get ServerLevel from
     * @return ServerLevel from World
     */
    public static ServerLevel getServerLevel(World world) {
        return ReflectionShortcuts.getServerLevel(world);
    }

}
