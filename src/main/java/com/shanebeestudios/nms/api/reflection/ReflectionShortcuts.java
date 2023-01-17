package com.shanebeestudios.nms.api.reflection;

import net.minecraft.world.level.WorldGenLevel;
import org.bukkit.World;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionShortcuts {

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

    public static WorldGenLevel getWorldGenLevel(World world) {
        try {
            return (WorldGenLevel) GET_HANDLE_METHOD.invoke(world);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
