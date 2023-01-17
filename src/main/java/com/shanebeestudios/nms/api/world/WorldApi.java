package com.shanebeestudios.nms.api.world;

import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "deprecation"})
public class WorldApi {

    private static final World DEFAULT_WORLD = Bukkit.getWorlds().get(0);

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
     * Get a list of all available biomes as {@link NamespacedKey NamespacedKeys}
     * Includes custom biomes as well
     *
     * @return List of biomes
     */
    public static List<NamespacedKey> getBiomeKeys() {
        WorldGenLevel worldGenLevel = ReflectionShortcuts.getWorldGenLevel(DEFAULT_WORLD);
        Registry<Biome> biomeRegistry = worldGenLevel.registryAccess().registryOrThrow(Registries.BIOME);

        List<NamespacedKey> keys = new ArrayList<>();
        biomeRegistry.keySet().forEach(resourceLocation -> {
            NamespacedKey namespacedKey = new NamespacedKey(resourceLocation.getNamespace(), resourceLocation.getPath());
            keys.add(namespacedKey);
        });
        return keys.stream().sorted(Comparator.comparing(NamespacedKey::toString)).collect(Collectors.toList());
    }

}
