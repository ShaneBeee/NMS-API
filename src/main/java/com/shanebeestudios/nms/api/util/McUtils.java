package com.shanebeestudios.nms.api.util;

import com.mojang.datafixers.util.Pair;
import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for changing Minecraft to/from Bukkit classes
 */
@SuppressWarnings("unused")
public class McUtils {

    /**
     * Get a Minecraft BlockPos from a Bukkit Location
     *
     * @param location Location to change to BlockPos
     * @return BlockPos from Location
     */
    public static BlockPos getPos(@NotNull Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Get a Minecraft Level and BlockPos from a Bukkit Location
     *
     * @param location Location to get world and pos from
     * @return Pair of Level and BlockPos
     */
    public static Pair<ServerLevel, BlockPos> getLevelPos(@NotNull Location location) {
        BlockPos pos = getPos(location);
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) {
            throw new IllegalArgumentException("Missing world in location");
        }
        ServerLevel serverLevel = getServerLevel(bukkitWorld);
        return new Pair<>(serverLevel, pos);
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
    public static ServerLevel getServerLevel(@NotNull World world) {
        return ReflectionShortcuts.getServerLevel(world);
    }

    /**
     * Get an instance of WorldGenLevel from a {@link World Bukkit World}
     *
     * @param world Bukkit world to get WorldGenLevel from
     * @return WorldGenLevel from Bukkit world
     */
    public static WorldGenLevel getWorldGenLevel(@NotNull World world) {
        return ReflectionShortcuts.getServerLevel(world);
    }

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

    /**
     * Get a Minecraft ServerPlayer from a Bukkit Player
     *
     * @param player Bukkit player to convert to NMS player
     * @return NMS player
     */
    @NotNull
    public static ServerPlayer getServerPlayer(@NotNull Player player) {
        return ReflectionShortcuts.getNMSPlayer(player);
    }

}
