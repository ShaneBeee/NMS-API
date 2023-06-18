package com.shanebeestudios.nms.api.world;

import com.mojang.datafixers.util.Pair;
import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Api methods pertaining to a {@link World}
 */
@SuppressWarnings({"unused", "deprecation"})
public class WorldApi {

    private static final StructureTemplateManager STRUCTURE_MANAGER = MinecraftServer.getServer().getStructureManager();
    private static final Registry<Biome> BIOME_REGISTRY = McUtils.getRegistry(Registries.BIOME);
    private static final Registry<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE_REGISTRY = McUtils.getRegistry(Registries.CONFIGURED_FEATURE);
    private static final Registry<PlacedFeature> PLACED_FEATURE_REGISTRY = McUtils.getRegistry(Registries.PLACED_FEATURE);
    private static final Registry<Structure> STRUCTURE_REGISTRY = McUtils.getRegistry(Registries.STRUCTURE);

    @Nullable
    private static <T> Holder.Reference<T> getHolderReference(Registry<T> registry, NamespacedKey key) {
        ResourceLocation resourceLocation = McUtils.getResourceLocation(key);
        ResourceKey<T> resourceKey = ResourceKey.create(registry.key(), resourceLocation);
        try {
            return registry.getHolderOrThrow(resourceKey);
        } catch (IllegalStateException ignore) {
            return null;
        }
    }

    @Nullable
    private static <T> T getRegistryValue(Registry<T> registry, NamespacedKey key) {
        Holder.Reference<T> holderReference = getHolderReference(registry, key);
        return holderReference != null ? holderReference.value() : null;
    }

    private static <T> List<NamespacedKey> getRegistryKeys(Registry<T> registry) {
        List<NamespacedKey> keys = new ArrayList<>();
        registry.keySet().forEach(resourceLocation -> {
            NamespacedKey namespacedKey = McUtils.getNamespacedKey(resourceLocation);
            keys.add(namespacedKey);
        });
        return keys.stream().sorted(Comparator.comparing(NamespacedKey::toString)).collect(Collectors.toList());
    }


    /**
     * Get the {@link NamespacedKey} of a {@link org.bukkit.block.Biome} at a specific location.
     * This will include custom biomes as well.
     *
     * @param location Location to grab biome at
     * @return Key of biome
     */
    @NotNull
    public static NamespacedKey getBiome(@NotNull Location location) {
        ServerLevel serverLevel = McUtils.getLevelPos(location).getFirst();

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Biome biome = serverLevel.getNoiseBiome(x >> 2, y >> 2, z >> 2).value();
        ResourceLocation key = BIOME_REGISTRY.getKey(biome);
        if (key == null) {
            // This shouldn't happen, but safety feature
            key = new ResourceLocation("minecraft", "plains");
        }
        return McUtils.getNamespacedKey(key);
    }

    /**
     * Set a biome at a location, including custom biomes.
     *
     * @param location Location of biome to change
     * @param biomeKey Key of biome
     */
    public static void setBiome(@NotNull Location location, @NotNull NamespacedKey biomeKey) {
        ServerLevel serverLevel = McUtils.getLevelPos(location).getFirst();
        Holder.Reference<Biome> biome = getHolderReference(BIOME_REGISTRY, biomeKey);

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        LevelChunk chunk = serverLevel.getChunkAt(new BlockPos(x, y, z));
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
    @Nullable
    public static Location locateBiome(@NotNull NamespacedKey biomeKey, @NotNull Location center) {
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
    @Nullable
    public static Location locateBiome(@NotNull NamespacedKey biomeKey, @NotNull Location center, int radius, int step) {
        Pair<ServerLevel, BlockPos> levelPos = McUtils.getLevelPos(center);
        BlockPos blockPos = levelPos.getSecond();
        ServerLevel level = levelPos.getFirst();
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
    @NotNull
    public static List<NamespacedKey> getBiomeKeys() {
        return getRegistryKeys(BIOME_REGISTRY);
    }

    /**
     * Place a configured feature
     *
     * @param featureKey Key of feature to place
     * @param location   Location to place at
     * @return True if feature was placed, otherwise false if failed
     * @see <a href="https://minecraft.fandom.com/wiki/Custom_feature?so=search#Configured_Feature">McWiki - Configured Feature</a>
     */
    public static boolean placeConfiguredFeature(@NotNull NamespacedKey featureKey, @NotNull Location location) {
        Pair<ServerLevel, BlockPos> levelPos = McUtils.getLevelPos(location);
        ServerLevel serverLevel = levelPos.getFirst();
        BlockPos blockPos = levelPos.getSecond();

        ConfiguredFeature<?, ?> configuredFeature = getRegistryValue(CONFIGURED_FEATURE_REGISTRY, featureKey);
        if (configuredFeature != null) {
            return configuredFeature.place(serverLevel, serverLevel.getChunkSource().getGenerator(), serverLevel.getRandom(), blockPos);
        }
        return false;
    }

    /**
     * Place a placed feature
     *
     * @param featureKey Key of feature to place
     * @param location   Location to place at
     * @return True if feature was placed, otherwise false if failed
     * @see <a href="https://minecraft.fandom.com/wiki/Custom_feature?so=search#Placed_feature">McWiki - Placed Feature</a>
     */
    public static boolean placePlacedFeature(@NotNull NamespacedKey featureKey, @NotNull Location location) {
        Pair<ServerLevel, BlockPos> levelPos = McUtils.getLevelPos(location);
        ServerLevel serverLevel = levelPos.getFirst();
        BlockPos blockPos = levelPos.getSecond();

        PlacedFeature placedFeature = getRegistryValue(PLACED_FEATURE_REGISTRY, featureKey);
        if (placedFeature != null) {
            return placedFeature.placeWithBiomeCheck(serverLevel, serverLevel.getChunkSource().getGenerator(), serverLevel.getRandom(), blockPos);
        }
        return false;
    }

    /**
     * Locate the nearest placed structure
     * <p>This does not mean a StructureBlock Structure, this means a structure
     * placed in a world, such as a village.
     * Will default to a radius of 6400 and true for findUnexplored.</p>
     *
     * @param structureKey Key of structure to find
     * @param location     Location to center search from
     * @return Location of structure if found, otherwise null
     */
    @Nullable
    public static Location locateNearestStructure(@NotNull NamespacedKey structureKey, @NotNull Location location) {
        return locateNearestStructure(structureKey, location, 6400, true);
    }

    /**
     * Locate the nearest placed structure
     * <p>This does not mean a StructureBlock Structure, this means a structure
     * placed in a world, such as a village.</p>
     *
     * @param structureKey   Key of structure to find
     * @param location       Location to center search from
     * @param radius         Max radius to search in
     * @param findUnexplored Whether to look for structures that haven't generated yet
     * @return Location of structure if found, otherwise null
     */
    @Nullable
    public static Location locateNearestStructure(@NotNull NamespacedKey structureKey, @NotNull Location location, int radius, boolean findUnexplored) {
        Pair<ServerLevel, BlockPos> levelPos = McUtils.getLevelPos(location);
        ServerLevel serverLevel = levelPos.getFirst();
        BlockPos blockPos = levelPos.getSecond();

        Holder.Reference<Structure> structureHolder = getHolderReference(STRUCTURE_REGISTRY, structureKey);
        if (structureHolder != null) {
            Pair<BlockPos, Holder<Structure>> nearestMapStructure = serverLevel.getChunkSource().getGenerator()
                    .findNearestMapStructure(serverLevel, HolderSet.direct(structureHolder), blockPos, radius, findUnexplored);
            if (nearestMapStructure != null) {
                return McUtils.getLocation(nearestMapStructure.getFirst(), serverLevel);
            }
        }
        return null;
    }

    /**
     * Get a list of all registered configured features
     *
     * @return List of all registered configured features
     */
    @NotNull
    public static List<NamespacedKey> getConfiguredFeatures() {
        return getRegistryKeys(CONFIGURED_FEATURE_REGISTRY);
    }

    /**
     * Get a list of all registered placed features
     *
     * @return List of all registered placed features
     */
    @NotNull
    public static List<NamespacedKey> getPlacedFeatures() {
        return getRegistryKeys(PLACED_FEATURE_REGISTRY);
    }

    /**
     * Get a list of available structures
     * <p>This does not mean StructureBlock Structures, this means structures
     * placed in a world, such as a village.</p>
     *
     * @return List of available structures
     */
    @NotNull
    public static List<NamespacedKey> getStructures() {
        return getRegistryKeys(STRUCTURE_REGISTRY);
    }

    /**
     * Get a list of available structure templates
     * <p>You can load via {@link org.bukkit.structure.StructureManager#getStructure(NamespacedKey)},
     * and place into world via {@link org.bukkit.structure.Structure#place(Location, boolean, StructureRotation, Mirror, int, float, Random)}</p>
     *
     * @return List of available structure templates
     */
    public static List<NamespacedKey> getStructureTemplates() {
        List<NamespacedKey> keys = new ArrayList<>();
        STRUCTURE_MANAGER.listTemplates().sorted(Comparator.comparing(ResourceLocation::toString)).forEach(resourceLocation -> keys.add(McUtils.getNamespacedKey(resourceLocation)));
        return keys;
    }

    /**
     * Check if a location is within a village
     *
     * @param location Location to check if in village
     * @return True if location is within a village
     */
    public static boolean isWithinVillage(Location location) {
        World world = location.getWorld();
        if (world != null) {
            ServerLevel serverLevel = McUtils.getServerLevel(world);
            BlockPos blockPos = McUtils.getPos(location);
            return serverLevel.isVillage(blockPos);
        }
        return false;
    }

}
