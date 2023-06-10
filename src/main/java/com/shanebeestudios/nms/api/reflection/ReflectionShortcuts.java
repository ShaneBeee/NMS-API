package com.shanebeestudios.nms.api.reflection;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Shortcuts for Minecraft using reflection
 */
public class ReflectionShortcuts {

    private static final World DEFAULT_WORLD = Bukkit.getWorlds().get(0);
    private static final Class<?> CRAFT_WORLD_CLASS;
    private static final Method CRAFT_WORLD_GET_HANDLE_METHOD;
    private static final Class<?> CRAFT_REGION_ACCESSOR_CLASS;
    private static final Method CRAFT_REGION_GET_HANDLE_METHOD;
    private static final Class<?> CRAFT_CHUNK_CLASS;
    private static final Method CRAFT_CHUNK_GET_HANDLE_METHOD;
    private static final Class<?> CRAFT_ITEM_STACK_CLASS;
    private static final Method CRAFT_ITEM_STACK_GET_NMS_ITEM_METHOD;
    private static final Field CRAFT_ITEM_STACK_HANDLE_FIELD;

    static {
        try {
            CRAFT_WORLD_CLASS = ReflectionUtils.getOBCClass("CraftWorld");
            CRAFT_REGION_ACCESSOR_CLASS = ReflectionUtils.getOBCClass("CraftRegionAccessor");
            CRAFT_CHUNK_CLASS = ReflectionUtils.getOBCClass("CraftChunk");
            CRAFT_ITEM_STACK_CLASS = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
            assert CRAFT_REGION_ACCESSOR_CLASS != null;
            assert CRAFT_WORLD_CLASS != null;
            assert CRAFT_CHUNK_CLASS != null;
            assert CRAFT_ITEM_STACK_CLASS != null;
            CRAFT_WORLD_GET_HANDLE_METHOD = CRAFT_WORLD_CLASS.getMethod("getHandle");
            CRAFT_REGION_GET_HANDLE_METHOD = CRAFT_REGION_ACCESSOR_CLASS.getMethod("getHandle");
            CRAFT_CHUNK_GET_HANDLE_METHOD = CRAFT_CHUNK_CLASS.getMethod("getHandle", ChunkStatus.class);
            CRAFT_ITEM_STACK_GET_NMS_ITEM_METHOD = CRAFT_ITEM_STACK_CLASS.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
            CRAFT_ITEM_STACK_HANDLE_FIELD = CRAFT_ITEM_STACK_CLASS.getField("handle");
        } catch (NoSuchMethodException | NoSuchFieldException e) {
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
        World bukkitWorld = world != null ? world : DEFAULT_WORLD;
        try {
            return (ServerLevel) CRAFT_WORLD_GET_HANDLE_METHOD.invoke(bukkitWorld);
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
    @NotNull
    public static ServerPlayer getNMSPlayer(@NotNull Player player) {
        net.minecraft.world.entity.Entity nmsEntity = getNMSEntity(player);
        if (nmsEntity instanceof ServerPlayer serverPlayer) return serverPlayer;
        throw new IllegalArgumentException("Player is null");
    }

    public static ItemStack getNMSItemStackCopy(org.bukkit.inventory.ItemStack bukkitStack) {
        try {
            Object invoke = CRAFT_ITEM_STACK_GET_NMS_ITEM_METHOD.invoke(null, bukkitStack);
            return ((ItemStack) invoke);
        } catch (IllegalAccessException | InvocationTargetException ignore) {
            return ItemStack.EMPTY;
        }
    }

    public static ItemStack getNMSItemStack(org.bukkit.inventory.ItemStack bukkitStack) {
        try {
            Object nmsStack = CRAFT_ITEM_STACK_HANDLE_FIELD.get(bukkitStack);
            return ((ItemStack) nmsStack);
        } catch (IllegalAccessException ignore) {
            return ItemStack.EMPTY;
        }
    }

}
