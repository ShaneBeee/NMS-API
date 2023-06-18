package com.shanebeestudios.nms.api.util;

/**
 * Utility class for math methods
 */
@SuppressWarnings("unused")
public class MathUtils {

    /** Clamp a float between a min and a max
     * @param num Float to clamp
     * @param min Min value
     * @param max Max value
     * @return Clamped float
     */
    public static float clamp(float num, float min, float max) {
        return Math.min(max, Math.max(num, min));
    }

    /** Clamp an int between a min and a max
     * @param num Int to clamp
     * @param min Min value
     * @param max Max value
     * @return Clamped int
     */
    public static int clamp(int num, int min, int max) {
        return Math.min(max, Math.max(num, min));
    }

}
