package com.shanebeestudios.nms.addon;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SkNMS extends JavaPlugin {

    private static SkNMS instance;

    @Override
    public void onEnable() {
        instance = this;
        if (Skript.isAcceptRegistrations()) {
            SkriptAddon skriptAddon = Skript.registerAddon(this);
            try {
                skriptAddon.loadClasses("com.shanebeestudios.nms.addon.elements");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onDisable() {
    }

    public static SkNMS getInstance() {
        return instance;
    }

}
