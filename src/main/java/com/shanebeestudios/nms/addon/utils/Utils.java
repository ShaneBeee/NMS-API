package com.shanebeestudios.nms.addon.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Utility class for simple shortcuts
 */
@SuppressWarnings("deprecation")
public class Utils {

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage(getColoredMessage("&7[&bSk&3NMS&7] &7" + message));
    }

    public static void error(String error) {
        Bukkit.getConsoleSender().sendMessage(getColoredMessage("&7[&cSk&4NMS&7] &e" + error));
    }

    public static String getColoredMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
