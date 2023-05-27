package com.shanebeestudios.nms.api.world;

import com.mojang.datafixers.util.Pair;
import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import com.shanebeestudios.nms.api.util.McUtils;
import com.shanebeestudios.nms.api.util.RegistryUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Api methods pertaining to a {@link World}
 */
@SuppressWarnings({"unused", "deprecation"})
public class WorldApi {

    private static final World DEFAULT_WORLD = Bukkit.getWorlds().get(0);
    private static final Registry<Biome> BIOME_REGISTRY = RegistryUtils.getRegistry(Registries.BIOME);
    private static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE_REGISTRY = RegistryUtils.getRegistry(Registries.CONFIGURED_FEATURE);
    private static final Registry<PlacedFeature> PLACED_FEATURE_REGISTRY = RegistryUtils.getRegistry(Registries.PLACED_FEATURE);
    private static final Registry<Structure> STRUCTURE_REGISTRY = RegistryUtils.getRegistry(Registries.STRUCTURE);


    /**
     * Get the {@link NamespacedKey} of a {@link org.bukkit.block.Biome} at a specific location.
     * This will include custom biomes as well.
     *
     * @param location Location to grab biome at
     * @return Key of biome
     */
    public static NamespacedKey getBiome(Location location) {
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) bukkitWorld = DEFAULT_WORLD;

        WorldGenLevel worldGenLevel = ReflectionShortcuts.getWorldGenLevel(bukkitWorld);

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Registry<Biome> biomeRegistry = worldGenLevel.registryAccess().registryOrThrow(Registries.BIOME);
        Biome biome = worldGenLevel.getNoiseBiome(x >> 2, y >> 2, z >> 2).value();
        ResourceLocation key = biomeRegistry.getKey(biome);
        if (key == null) {
            // This shouldn't happen, but safety feature
            key = new ResourceLocation("minecraft", "plains");
        }
        return new NamespacedKey(key.getNamespace(), key.getPath());
    }

    /**
     * Set a biome at a location, including custom biomes.
     *
     * @param location      Location of biome to change
     * @param namespacedKey Key of biome
     */
    public static void setBiome(Location location, NamespacedKey namespacedKey) {
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) bukkitWorld = DEFAULT_WORLD;

        WorldGenLevel worldGenLevel = ReflectionShortcuts.getWorldGenLevel(bukkitWorld);

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        ResourceLocation resourceLocation = new ResourceLocation(namespacedKey.getNamespace(), namespacedKey.getKey());
        ResourceKey<Biome> biomeResourceKey = ResourceKey.create(Registries.BIOME, resourceLocation);
        Holder.Reference<Biome> biome = BIOME_REGISTRY.getHolderOrThrow(biomeResourceKey);

        LevelChunk chunk = worldGenLevel.getMinecraftWorld().getChunkAt(new BlockPos(x, y, z));
        chunk.setBiome(x >> 2, y >> 2, z >> 2, biome);
        chunk.setUnsaved(true);
    }

    /**
     * Locate a biome in a radius of a location
     * <p>Defaults to 6400 radius with 8 steps</p>
     *
     * @param biomeKey NamespacedKey of biome to find
     * @param center   Where to look from
     * @return Location if a biome is found, null otherwise
     */
    public static Location locateBiome(NamespacedKey biomeKey, Location center) {
        return locateBiome(biomeKey, center, 6400, 8);
    }

    /**
     * Locate a biome in a radius of a location
     *
     * @param biomeKey NamespacedKey of biome to find
     * @param center   Where to look from
     * @param radius   Max radius to search
     * @param step     How many blocks to check in steps
     * @return Location if a biome is found, null otherwise
     */
    public static Location locateBiome(NamespacedKey biomeKey, Location center, int radius, int step) {
        BlockPos blockPos = McUtils.getPos(center);
        ServerLevel level = McUtils.getServerLevel(center.getWorld());
        ResourceLocation resourceLocation = McUtils.getResourceLocation(biomeKey);
        Pair<BlockPos, Holder<Biome>> closestBiome3d = level.findClosestBiome3d(holder ->
                holder.is(resourceLocation), blockPos, radius, step, 64);

        if (closestBiome3d == null) return null;
        BlockPos biomePos = closestBiome3d.getFirst();
        return McUtils.getLocation(biomePos, level);
    }

    /**
     * Get a list of all available biomes as {@link NamespacedKey NamespacedKeys}
     * Includes custom biomes as well
     *
     * @return List of biomes
     */
    public static List<NamespacedKey> getBiomeKeys() {
        WorldGenLevel worldGenLevel = ReflectionShortcuts.getWorldGenLevel(DEFAULT_WORLD);

        List<NamespacedKey> keys = new ArrayList<>();
        BIOME_REGISTRY.keySet().forEach(resourceLocation -> {
            NamespacedKey namespacedKey = new NamespacedKey(resourceLocation.getNamespace(), resourceLocation.getPath());
            keys.add(namespacedKey);
        });
        return keys.stream().sorted(Comparator.comparing(NamespacedKey::toString)).collect(Collectors.toList());
    }

    /**
     * Place a configured feature
     *
     * @param featureKey Key of feature to place
     * @param location   Location to place at
     * @return True if feature was placed, otherwise false if failed
     */
    public static boolean placeConfiguredFeature(NamespacedKey featureKey, Location location) {
        World bukkitWorld = location.getWorld() == null ? DEFAULT_WORLD : location.getWorld();
        WorldGenLevel worldGenLevel = ReflectionShortcuts.getWorldGenLevel(bukkitWorld);
        ServerLevel serverLevel = worldGenLevel.getLevel();
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        ResourceLocation featureLocation = new ResourceLocation(featureKey.getNamespace(), featureKey.getKey());

        ResourceKey<ConfiguredFeature<?, ?>> placedFeatureResourceKey = ResourceKey.create(CONFIGURED_FEATURE_REGISTRY.key(), featureLocation);
        Holder.Reference<ConfiguredFeature<?, ?>> placedFeatureHolder = CONFIGURED_FEATURE_REGISTRY.getHolderOrThrow(placedFeatureResourceKey);
        ConfiguredFeature<?, ?> configuredFeature = placedFeatureHolder.value();
        return configuredFeature.place(worldGenLevel, serverLevel.getChunkSource().getGenerator(), serverLevel.getRandom(), blockPos);
    }

    /**
     * Place a placed feature
     *
     * @param featureKey Key of feature to place
     * @param location   Location to place at
     * @return True if feature was placed, otherwise false if failed
     */
    public static boolean placePlacedFeature(NamespacedKey featureKey, Location location) {
        World bukkitWorld = location.getWorld() == null ? DEFAULT_WORLD : location.getWorld();
        WorldGenLevel worldGenLevel = ReflectionShortcuts.getWorldGenLevel(bukkitWorld);
        ServerLevel serverLevel = worldGenLevel.getLevel();
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        ResourceLocation featureLocation = new ResourceLocation(featureKey.getNamespace(), featureKey.getKey());

        ResourceKey<PlacedFeature> placedFeatureResourceKey = ResourceKey.create(PLACED_FEATURE_REGISTRY.key(), featureLocation);
        Holder.Reference<PlacedFeature> placedFeatureHolder = PLACED_FEATURE_REGISTRY.getHolderOrThrow(placedFeatureResourceKey);
        PlacedFeature placedFeature = placedFeatureHolder.value();
        return placedFeature.placeWithBiomeCheck(worldGenLevel, serverLevel.getChunkSource().getGenerator(), serverLevel.getRandom(), blockPos);
    }

    /**
     * Locate the nearest placed structure
     * <p>This does not mean a StructureBlock Structure, this means a structure
     * placed in a world, such as a village.
     * Will default to a radius of 6400 and true for findUnexplored.</p>
     *
     * @param key      Key of structure to find
     * @param location Location to center search from
     * @return Location of structure if found, otherwise null
     */
    public static Location locateNearestStructure(NamespacedKey key, Location location) {
        return locateNearestStructure(key, location, 6400, true);
    }

    /**
     * Locate the nearest placed structure
     * <p>This does not mean a StructureBlock Structure, this means a structure
     * placed in a world, such as a village.</p>
     *
     * @param key            Key of structure to find
     * @param location       Location to center search from
     * @param radius         Max radius to search in
     * @param findUnexplored Whether to look for structures that haven't generated yet
     * @return Location of structure if found, otherwise null
     */
    public static Location locateNearestStructure(NamespacedKey key, Location location, int radius, boolean findUnexplored) {
        World bukkitWorld = location.getWorld() == null ? DEFAULT_WORLD : location.getWorld();
        ServerLevel serverLevel = ReflectionShortcuts.getServerLevel(bukkitWorld);
        BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        ResourceLocation featureLocation = new ResourceLocation(key.getNamespace(), key.getKey());
        ResourceKey<Structure> structureResourceKey = ResourceKey.create(STRUCTURE_REGISTRY.key(), featureLocation);
        Holder.Reference<Structure> structureHolder = STRUCTURE_REGISTRY.getHolderOrThrow(structureResourceKey);

        Pair<BlockPos, Holder<Structure>> nearestMapStructure = serverLevel.getChunkSource().getGenerator()
                .findNearestMapStructure(serverLevel, HolderSet.direct(structureHolder), blockPos, radius, findUnexplored);
        if (nearestMapStructure != null) {
            return McUtils.getLocation(nearestMapStructure.getFirst(), serverLevel);
        }
        return null;
    }

    /**
     * Get a list of all registered configured features
     *
     * @return List of all registered configured features
     */
    public static List<NamespacedKey> getConfiguredFeatures() {
        List<NamespacedKey> keys = new ArrayList<>();
        CONFIGURED_FEATURE_REGISTRY.keySet().forEach(resourceLocation -> {
            NamespacedKey namespacedKey = new NamespacedKey(resourceLocation.getNamespace(), resourceLocation.getPath());
            keys.add(namespacedKey);
        });
        return keys.stream().sorted(Comparator.comparing(NamespacedKey::toString)).collect(Collectors.toList());
    }

    /**
     * Get a list of all registered placed features
     *
     * @return List of all registered placed features
     */
    public static List<NamespacedKey> getPlacedFeatures() {
        List<NamespacedKey> keys = new ArrayList<>();
        PLACED_FEATURE_REGISTRY.keySet().forEach(resourceLocation -> {
            NamespacedKey namespacedKey = new NamespacedKey(resourceLocation.getNamespace(), resourceLocation.getPath());
            keys.add(namespacedKey);
        });
        return keys.stream().sorted(Comparator.comparing(NamespacedKey::toString)).collect(Collectors.toList());
    }

    /**
     * Get a list of available structures
     * <p>This does not mean StructureBlock Structures, this means structures
     * placed in a world, such as a village.</p>
     *
     * @return List of available structures
     */
    public static List<NamespacedKey> getStructures() {
        List<NamespacedKey> keys = new ArrayList<>();
        STRUCTURE_REGISTRY.keySet().forEach(resourceLocation -> {
            NamespacedKey namespacedKey = new NamespacedKey(resourceLocation.getNamespace(), resourceLocation.getPath());
            keys.add(namespacedKey);
        });
        return keys.stream().sorted(Comparator.comparing(NamespacedKey::toString)).collect(Collectors.toList());
    }

}
