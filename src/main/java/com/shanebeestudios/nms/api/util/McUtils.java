package com.shanebeestudios.nms.api.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableInt;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utility class for changing Minecraft to/from Bukkit classes
 */
@SuppressWarnings("unused")
public class McUtils {

    private static final BlockData AIR = Material.AIR.createBlockData();

    /**
     * Get a Minecraft BlockPos from a Bukkit Location
     *
     * @param location Location to change to BlockPos
     * @return BlockPos from Location
     */
    @NotNull
    public static BlockPos getPos(@NotNull Location location) {
        return new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Get a Minecraft Vec# from a Bukkit Location
     *
     * @param location Location to convert to Vec3
     * @return Vec3 from Location
     */
    @NotNull
    public static Vec3 getVec3(Location location) {
        return new Vec3(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Convert Bukkit BlockFace to Minecraft Direction
     *
     * @param blockFace BlockFace to convert
     * @return Minecraft Direction from BlockFace
     */
    @NotNull
    public static Direction getDirection(BlockFace blockFace) {
        if (blockFace == null) return Direction.UP;
        return switch (blockFace) {
            case DOWN -> Direction.DOWN;
            case NORTH -> Direction.NORTH;
            case SOUTH -> Direction.SOUTH;
            case EAST -> Direction.EAST;
            case WEST -> Direction.WEST;
            default -> Direction.UP;
        };
    }

    /**
     * Get a Minecraft Level and BlockPos from a Bukkit Location
     *
     * @param location Location to get world and pos from
     * @return Pair of Level and BlockPos
     */
    @NotNull
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
    @NotNull
    public static Location getLocation(BlockPos blockPos, Level level) {
        return new Location(level.getWorld(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    /**
     * Convert a Bukkit NamespacedKey to Minecraft ResourceLocation
     *
     * @param bukkitKey NamespacedKey to change to ResourceLocation
     * @return ResourceLocation from NamespacedKey
     */
    @NotNull
    public static ResourceLocation getResourceLocation(NamespacedKey bukkitKey) {
        return new ResourceLocation(bukkitKey.getNamespace(), bukkitKey.getKey());
    }

    /**
     * Convert Minecraft ResourceLocation to Bukkit NamespacedKey
     *
     * @param resourceLocation ResourceLocation to change to NamespacedKey
     * @return ResourceLocation from NamespacedKey
     */
    @NotNull
    public static NamespacedKey getNamespacedKey(ResourceLocation resourceLocation) {
        return new NamespacedKey(resourceLocation.getNamespace(), resourceLocation.getPath());
    }

    /**
     * Get an instance of ServerLevel from a {@link World Bukkit World}
     *
     * @param world World to get ServerLevel from
     * @return ServerLevel from World
     */
    @NotNull
    public static ServerLevel getServerLevel(@NotNull World world) {
        return ReflectionShortcuts.getServerLevel(world);
    }

    /**
     * Get an instance of WorldGenLevel from a {@link World Bukkit World}
     *
     * @param world Bukkit world to get WorldGenLevel from
     * @return WorldGenLevel from Bukkit world
     */
    @NotNull
    public static WorldGenLevel getWorldGenLevel(@NotNull World world) {
        return ReflectionShortcuts.getWorldGenLevel(world);
    }

    /**
     * Get a Minecraft Registry
     *
     * @param registry ResourceKey of registry
     * @param <T>      ResourceKey
     * @return Registry from key
     */
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

    /**
     * Get a {@link BlockData Bukkit Blockdata} from a Minecraft BlockState
     *
     * @param blockState BlockState to convert
     * @return BlockData from state
     */
    @NotNull
    public static BlockData getBlockDataFromState(BlockState blockState) {
        BlockData blockDataFromBlockState = ReflectionShortcuts.getBlockDataFromBlockState(blockState);
        return blockDataFromBlockState != null ? blockDataFromBlockState : AIR;
    }

    /**
     * Get a Minecraft BlockState from a Bukkit Block
     *
     * @param bukkitBlock Bukkit Block to grab state from
     * @return BlockState from Bukkit Block
     */
    @NotNull
    public static BlockState getBlockStateFromBlock(Block bukkitBlock) {
        return ReflectionShortcuts.getBlockStateFromBlock(bukkitBlock);
    }

    /**
     * Get a Minecraft BlockState from a Bukkit BlockData
     *
     * @param blockData BlockData to convert
     * @return Converted BlockState
     */
    @NotNull
    public static BlockState getBlockStateFromData(BlockData blockData) {
        return ReflectionShortcuts.getBlockStateFromData(blockData);
    }

    /**
     * Get a holder reference from a registry
     *
     * @param registry Registry to grab holder from
     * @param key      Key of holder
     * @param <T>      Class type of registry
     * @return Holder from registry
     */
    @Nullable
    public static <T> Holder.Reference<T> getHolderReference(Registry<T> registry, NamespacedKey key) {
        ResourceLocation resourceLocation = McUtils.getResourceLocation(key);
        ResourceKey<T> resourceKey = ResourceKey.create(registry.key(), resourceLocation);
        try {
            return registry.getHolderOrThrow(resourceKey);
        } catch (IllegalStateException ignore) {
            return null;
        }
    }

    /**
     * Get a keyed value from a registry
     *
     * @param registry Registry to grab value from
     * @param key      Key of value to grab
     * @param <T>      Registry class type
     * @return Value from registry
     */
    @Nullable
    public static <T> T getRegistryValue(Registry<T> registry, NamespacedKey key) {
        Holder.Reference<T> holderReference = getHolderReference(registry, key);
        return holderReference != null ? holderReference.value() : null;
    }

    /**
     * Get all keys from a registry
     *
     * @param registry Registry to grab keys from
     * @param <T>      Registry class type
     * @return List of NamespacedKeys for all keys in registry
     */
    @NotNull
    public static <T> List<NamespacedKey> getRegistryKeys(Registry<T> registry) {
        List<NamespacedKey> keys = new ArrayList<>();
        registry.keySet().forEach(resourceLocation -> {
            NamespacedKey namespacedKey = McUtils.getNamespacedKey(resourceLocation);
            keys.add(namespacedKey);
        });
        return keys.stream().sorted(Comparator.comparing(NamespacedKey::toString)).collect(Collectors.toList());
    }

    /**
     * Shift coordinates for 3D biomes
     *
     * @param coordinate Coordinate to shift
     * @return Shifted coordinate
     */
    public static int quantize(int coordinate) {
        return QuartPos.toBlock(QuartPos.fromBlock(coordinate));
    }

    /**
     * Shift blockPos for 3D biomes
     *
     * @param pos BlockPos to shift
     * @return Shifted BlockPos
     */
    public static BlockPos quantize(BlockPos pos) {
        return new BlockPos(quantize(pos.getX()), quantize(pos.getY()), quantize(pos.getZ()));
    }

    /**
     * Make a resolver for 3D shifted biomes
     *
     * @param count       counter
     * @param chunkAccess Chunk where biome is
     * @param box         BoundingBox for biome change
     * @param biome       Biome
     * @param filter      Filter
     * @return Biome resolver
     */
    @NotNull
    public static BiomeResolver getBiomeResolver(MutableInt count, ChunkAccess chunkAccess, BoundingBox box, Holder<Biome> biome, Predicate<Holder<Biome>> filter) {
        return (x, y, z, noise) -> {
            int i = QuartPos.toBlock(x);
            int j = QuartPos.toBlock(y);
            int k = QuartPos.toBlock(z);
            Holder<Biome> biomeHolder = chunkAccess.getNoiseBiome(x, y, z);
            if (box.isInside(i, j, k) && filter.test(biomeHolder)) {
                count.increment();
                return biome;
            } else {
                return biomeHolder;
            }
        };
    }


    /**
     * Set the skin of a GameProfile
     *
     * @param name        Name of player
     * @param gameProfile Profile to set
     */
    // Courtesy of Jonas2004
    // https://www.spigotmc.org/threads/create-fake-player-1-20-2.621480/#post-4649259
    public static void setSkin(String name, GameProfile gameProfile) {
        Gson gson = new Gson();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        String json = getStringFromURL(url);
        String uuid = gson.fromJson(json, JsonObject.class).get("id").getAsString();

        url = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false";
        json = getStringFromURL(url);
        JsonObject mainObject = gson.fromJson(json, JsonObject.class);
        JsonObject jsonObject = mainObject.get("properties").getAsJsonArray().get(0).getAsJsonObject();
        String value = jsonObject.get("value").getAsString();
        String signature = jsonObject.get("signature").getAsString();
        PropertyMap propertyMap = gameProfile.getProperties();
        propertyMap.put("name", new Property("name", name));
        propertyMap.put("textures", new Property("textures", value, signature));
    }

    private static String getStringFromURL(String url) {
        StringBuilder text = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new URL(url).openStream());
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                while (line.startsWith(" ")) {
                    line = line.substring(1);
                }
                text.append(line);
            }
            scanner.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return text.toString();
    }

    /** Get the Minecraft Server
     * @param server Bukkit Server to convert to Minecraft Server
     * @return Minecraft Server from Bukkit Server
     */
    public static DedicatedServer getMinecraftServer(Server server) {
        return ReflectionShortcuts.getMinecraftServer(server);
    }

}
