package com.shanebeestudios.nms.api.world.entity;

import com.mojang.authlib.GameProfile;
import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import com.shanebeestudios.nms.api.reflection.ReflectionUtils;
import com.shanebeestudios.nms.api.world.WorldApi;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Api methods pertaining to an {@link org.bukkit.entity.Entity}
 */
@SuppressWarnings({"deprecation", "unused"})
public class EntityApi {

    /** Get an NMS Entity from a {@link org.bukkit.entity.Entity Bukkit Entity}
     *
     * @param bukkitEntity Bukkit Entity
     * @return NMS Entity
     */
    public static Entity getNMSEntity(org.bukkit.entity.Entity bukkitEntity) {
        return (Entity) ReflectionUtils.getNMSEntity(bukkitEntity);
    }

}
