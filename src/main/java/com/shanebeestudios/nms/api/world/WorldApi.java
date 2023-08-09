package com.shanebeestudios.nms.api.world;

import com.mojang.datafixers.util.Pair;
import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * API methods relating to {@link World Worlds}
 */
@SuppressWarnings({"unused"})
public class WorldApi {

    private WorldApi() {
    }

    private static final Registry<Biome> BIOME_REGISTRY = McUtils.getRegistry(Registries.BIOME);


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
        Holder.Reference<Biome> biome = McUtils.getHolderReference(BIOME_REGISTRY, biomeKey);

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
        return McUtils.getRegistryKeys(BIOME_REGISTRY);
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
