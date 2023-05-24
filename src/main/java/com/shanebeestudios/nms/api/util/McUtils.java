package com.shanebeestudios.nms.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;

/**
 * Utility class for changing Minecraft to/from Bukkit classes
 */
public class McUtils {

    public static BlockPos locationToPos(Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Location posToLocation(BlockPos blockPos, Level level) {
        return new Location(level.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    public static ResourceLocation getResourceLocation(NamespacedKey bukkitKey) {
        return new ResourceLocation(bukkitKey.getNamespace(), bukkitKey.getKey());
    }

}
