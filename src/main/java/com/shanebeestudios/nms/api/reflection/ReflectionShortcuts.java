package com.shanebeestudios.nms.api.reflection;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Shortcuts for Minecraft using reflection
 */
public class ReflectionShortcuts {

    private static final Class<?> CRAFT_WORLD_CLASS;
    private static final Method CRAFT_WORLD_GET_HANDLE_METHOD;
    private static final Class<?> CRAFT_REGION_ACCESSOR_CLASS;
    private static final Method CRAFT_REGION_GET_HANDLE_METHOD;
    private static final Class<?> CRAFT_CHUNK_CLASS;
    private static final Method CRAFT_CHUNK_GET_HANDLE_METHOD;

    static {
        try {
            CRAFT_WORLD_CLASS = ReflectionUtils.getOBCClass("CraftWorld");
            CRAFT_REGION_ACCESSOR_CLASS = ReflectionUtils.getOBCClass("CraftRegionAccessor");
            CRAFT_CHUNK_CLASS = ReflectionUtils.getOBCClass("CraftChunk");
            assert CRAFT_REGION_ACCESSOR_CLASS != null;
            assert CRAFT_WORLD_CLASS != null;
            assert CRAFT_CHUNK_CLASS != null;
            CRAFT_WORLD_GET_HANDLE_METHOD = CRAFT_WORLD_CLASS.getMethod("getHandle");
            CRAFT_REGION_GET_HANDLE_METHOD = CRAFT_REGION_ACCESSOR_CLASS.getMethod("getHandle");
            CRAFT_CHUNK_GET_HANDLE_METHOD = CRAFT_CHUNK_CLASS.getMethod("getHandle", ChunkStatus.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get an instance of WorldGenLevel from a {@link World Bukkit World}
     *
     * @param world Bukkit world to get WorldGenLevel from
     * @return WorldGenLevel from Bukkit world
     */
    public static WorldGenLevel getWorldGenLevel(World world) {
        try {
            return (WorldGenLevel) CRAFT_REGION_GET_HANDLE_METHOD.invoke(world);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get an instance of ServerLevel from a {@link World Bukkit World}
     *
     * @param world World to get ServerLevel from
     * @return ServerLevel from World
     */
    public static ServerLevel getServerLevel(World world) {
        try {
            return (ServerLevel) CRAFT_WORLD_GET_HANDLE_METHOD.invoke(world);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static LevelChunk getLevelChunk(Chunk chunk) {
        try {
            return (LevelChunk) CRAFT_CHUNK_GET_HANDLE_METHOD.invoke(chunk, ChunkStatus.FULL);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get an NMS entity
     *
     * @param entity Bukkit entity to get NMS entity from
     * @return NMS entity
     */
    @Nullable
    public static net.minecraft.world.entity.Entity getNMSEntity(Entity entity) {
        try {
            Method getHandle = entity.getClass().getMethod("getHandle");
            Object invoke = getHandle.invoke(entity);
            if (invoke instanceof net.minecraft.world.entity.Entity entity1) return entity1;
            return null;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get an NMS player
     *
     * @param player Bukkit player to get NMS player from
     * @return NMS player
     */
    @Nullable
    public static net.minecraft.world.entity.player.Player getNMSPlayer(Player player) {
        net.minecraft.world.entity.Entity nmsEntity = getNMSEntity(player);
        if (nmsEntity instanceof net.minecraft.world.entity.player.Player player1) return player1;
        return null;
    }

}
