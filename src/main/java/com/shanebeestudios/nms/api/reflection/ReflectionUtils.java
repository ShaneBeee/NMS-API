package com.shanebeestudios.nms.api.reflection;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;

/**
 * Utility methods for simple use of reflection
 */
@SuppressWarnings("unused")
public class ReflectionUtils {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
    private static final boolean NEW_NMS = isRunningMinecraft(1, 17);

    /**
     * Get a class from the {@link org.bukkit.craftbukkit} package
     *
     * @param obcClassString Class name (including subpackages)
     * @return Class object
     */
    public static Class<?> getOBCClass(String obcClassString) {
        String name = "org.bukkit.craftbukkit." + VERSION + obcClassString;
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get an NMS (net.minecraft.server) class
     *
     * @param nmsClass   NMS class name
     * @param nmsPackage vanilla MC mapped package
     * @return Class object
     */
    public static Class<?> getNMSClass(String nmsClass, String nmsPackage) {
        try {
            if (NEW_NMS) {
                return Class.forName(nmsPackage + "." + nmsClass);
            } else {
                return Class.forName("net.minecraft.server." + VERSION + nmsClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a field object
     *
     * @param field  Name of field
     * @param clazz  Class containing field
     * @param object Object to grab field from (null if static field)
     * @return Field object from class
     */
    public static Object getField(String field, Class<?> clazz, Object object) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Set a field
     *
     * @param field  Name of field
     * @param clazz  Class containing field
     * @param object Object with field (null if static field)
     * @param toSet  new value of field
     */
    public static void setField(String field, Class<?> clazz, Object object, Object toSet) {
        try {
            Field f = clazz.getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Set a field
     *
     * @param field  Name of field
     * @param object Object with field (null if static field)
     * @param toSet  new value of field
     */
    public static void setField(String field, Object object, Object toSet) {
        try {
            Field f = object.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, toSet);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Check if server is running a minimum Minecraft version
     *
     * @param major Major version to check (Most likely just going to be 1)
     * @param minor Minor version to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor) {
        return isRunningMinecraft(major, minor, 0);
    }

    /**
     * Check if server is running a minimum Minecraft version
     *
     * @param major    Major version to check (Most likely just going to be 1)
     * @param minor    Minor version to check
     * @param revision Revision to check
     * @return True if running this version or higher
     */
    public static boolean isRunningMinecraft(int major, int minor, int revision) {
        String[] version = Bukkit.getServer().getBukkitVersion().split("-")[0].split("\\.");
        int maj = Integer.parseInt(version[0]);
        int min = Integer.parseInt(version[1]);
        int rev;
        try {
            rev = Integer.parseInt(version[2]);
        } catch (Exception ignore) {
            rev = 0;
        }
        return maj > major || min > minor || (min == minor && rev >= revision);
    }

}
