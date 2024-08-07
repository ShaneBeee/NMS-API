package com.shanebeestudios.nms.api.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

/**
 * Utility class with shortcut methods for reflection
 */
public class ReflectionUtils {

    /**
     * Get the value of a field from an object
     *
     * @param field  Name of field
     * @param clazz  Class with field
     * @param object Object which contains field
     * @return Object from field
     */
    public static @Nullable Object getField(String field, Class<?> clazz, Object object) {
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
     * Set the value of a field in an object
     *
     * @param field  Name of field to set
     * @param clazz  Class with field
     * @param object Object which holds field
     * @param toSet  Object to set
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
     * Set the value of a field in an object
     *
     * @param field  Name of field to set
     * @param object Object which holds field
     * @param toSet  Object to set
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

}
