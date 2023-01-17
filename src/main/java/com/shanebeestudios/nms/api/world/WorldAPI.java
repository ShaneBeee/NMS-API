package com.shanebeestudios.nms.api.world;

import com.shanebeestudios.nms.api.reflection.ReflectionUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({"unused", "deprecation"})
public class WorldAPI {

    private static final World DEFAULT_WORLD = Bukkit.getWorlds().get(0);

    public static NamespacedKey getBiome(Location location) {
        World bukkitWorld = location.getWorld();
        if (bukkitWorld == null) bukkitWorld = DEFAULT_WORLD;

        WorldGenLevel handle = getWorldGenLevel(bukkitWorld);

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Registry<Biome> biomeRegistry = handle.registryAccess().registryOrThrow(Registries.BIOME);
        Biome biome = handle.getNoiseBiome(x >> 2, y >> 2, z >> 2).value();
        ResourceLocation key = biomeRegistry.getKey(biome);
        if (key == null) {
            // This shouldn't happen, but safety feature
            key = new ResourceLocation("minecraft", "plains");
        }
        return new NamespacedKey(key.getNamespace(), key.getPath());
    }

    private static final Class<?> CRAFT_REGION_ACCESSOR_CLASS;
    private static final Method GET_HANDLE_METHOD;

    static {
        try {
            CRAFT_REGION_ACCESSOR_CLASS = ReflectionUtils.getOBCClass("CraftRegionAccessor");
            assert CRAFT_REGION_ACCESSOR_CLASS != null;
            GET_HANDLE_METHOD = CRAFT_REGION_ACCESSOR_CLASS.getMethod("getHandle");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static WorldGenLevel getWorldGenLevel(World world) {
        try {
            return (WorldGenLevel) GET_HANDLE_METHOD.invoke(world);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
