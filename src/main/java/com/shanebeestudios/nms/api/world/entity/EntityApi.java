package com.shanebeestudios.nms.api.world.entity;

import com.shanebeestudios.nms.api.reflection.ReflectionUtils;
import net.minecraft.world.entity.Entity;

/**
 * Api methods pertaining to an {@link org.bukkit.entity.Entity}
 */
@SuppressWarnings({"deprecation", "unused"})
public class EntityApi {

    /**
     * Get an NMS Entity from a {@link org.bukkit.entity.Entity Bukkit Entity}
     *
     * @param bukkitEntity Bukkit Entity
     * @return NMS Entity
     */
    public static Entity getNMSEntity(org.bukkit.entity.Entity bukkitEntity) {
        return (Entity) ReflectionUtils.getNMSEntity(bukkitEntity);
    }

}
