package com.shanebeestudios.nms.api.reflection;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
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
    private static final Class<?> CRAFT_BLOCK_DATA_CLASS;
    private static final Method CRAFT_BLOCK_DATA_FROM_DATA_METHOD;
    private static final Method CRAFT_BLOCK_DATA_GET_STATE_METHOD;
    private static final Class<?> CRAFT_BLOCK_CLASS;
    private static final Method CRAFT_BLOCK_GET_NMS_METHOD;
    private static final Class<?> CRAFT_MAGIC_NUMBERS_CLASS;
    private static final Method CRAFT_MAGIC_NUMBERS_ITEM_METHOD;

    static {
        try {
            CRAFT_WORLD_CLASS = ReflectionUtils.getOBCClass("CraftWorld");
            CRAFT_REGION_ACCESSOR_CLASS = ReflectionUtils.getOBCClass("CraftRegionAccessor");
            CRAFT_CHUNK_CLASS = ReflectionUtils.getOBCClass("CraftChunk");
            CRAFT_ITEM_STACK_CLASS = ReflectionUtils.getOBCClass("inventory.CraftItemStack");
            CRAFT_BLOCK_DATA_CLASS = ReflectionUtils.getOBCClass("block.data.CraftBlockData");
            CRAFT_BLOCK_CLASS = ReflectionUtils.getOBCClass("block.CraftBlock");
            CRAFT_MAGIC_NUMBERS_CLASS = ReflectionUtils.getOBCClass("util.CraftMagicNumbers");
            assert CRAFT_REGION_ACCESSOR_CLASS != null;
            assert CRAFT_WORLD_CLASS != null;
            assert CRAFT_CHUNK_CLASS != null;
            assert CRAFT_ITEM_STACK_CLASS != null;
            assert CRAFT_BLOCK_DATA_CLASS != null;
            assert CRAFT_BLOCK_CLASS != null;
            assert CRAFT_MAGIC_NUMBERS_CLASS != null;
            CRAFT_WORLD_GET_HANDLE_METHOD = CRAFT_WORLD_CLASS.getMethod("getHandle");
            CRAFT_REGION_GET_HANDLE_METHOD = CRAFT_REGION_ACCESSOR_CLASS.getMethod("getHandle");
            CRAFT_CHUNK_GET_HANDLE_METHOD = CRAFT_CHUNK_CLASS.getMethod("getHandle", ChunkStatus.class);
            CRAFT_ITEM_STACK_GET_NMS_ITEM_METHOD = CRAFT_ITEM_STACK_CLASS.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
            CRAFT_ITEM_STACK_HANDLE_FIELD = CRAFT_ITEM_STACK_CLASS.getField("handle");
            CRAFT_BLOCK_DATA_FROM_DATA_METHOD = CRAFT_BLOCK_DATA_CLASS.getMethod("fromData", BlockState.class);
            CRAFT_BLOCK_DATA_GET_STATE_METHOD = CRAFT_BLOCK_DATA_CLASS.getMethod("getState");
            CRAFT_BLOCK_GET_NMS_METHOD = CRAFT_BLOCK_CLASS.getMethod("getNMS");
            CRAFT_MAGIC_NUMBERS_ITEM_METHOD = CRAFT_MAGIC_NUMBERS_CLASS.getMethod("getItem", Material.class);
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

    public static Item getItemFromMaterial(Material material) {
        try {
            Object itemObject = CRAFT_MAGIC_NUMBERS_ITEM_METHOD.invoke(null, material);
            if (itemObject instanceof Item item) return item;
            return Items.AIR;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static BlockData getBlockDataFromBlockState(BlockState blockState) {
        try {
            Object invoke = CRAFT_BLOCK_DATA_FROM_DATA_METHOD.invoke(CRAFT_BLOCK_DATA_CLASS, blockState);
            return ((BlockData) invoke);
        } catch (IllegalAccessException | InvocationTargetException ignore) {
            return null;
        }
    }

    public static BlockState getBlockStateFromBlock(Block bukkitBlock) {
        try {
            Object blockStateObject = CRAFT_BLOCK_GET_NMS_METHOD.invoke(bukkitBlock);
            if (blockStateObject instanceof BlockState blockState) return blockState;
            return null;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static BlockState getBlockStateFromData(BlockData blockData) {
        try {
            Object blockStateObject = CRAFT_BLOCK_DATA_GET_STATE_METHOD.invoke(blockData);
            if (blockStateObject instanceof BlockState blockState) return blockState;
            return null;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
