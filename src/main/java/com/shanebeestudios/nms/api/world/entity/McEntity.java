package com.shanebeestudios.nms.api.world.entity;

import com.shanebeestudios.nms.api.world.McLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftEntity;

/**
 * Wrapper for Minecraft Entity
 */
@SuppressWarnings("unused")
public class McEntity {

    /**
     * Wrap a Minecraft Entity
     *
     * @param nmsEntity Minecraft Entity to wrap
     * @return Wrapped Entity
     */
    public static McEntity wrap(Entity nmsEntity) {
        return new McEntity(nmsEntity);
    }

    /**
     * Wrap a Bukkit Entity
     *
     * @param bukkitEntity Bukkit Entity to wrap
     * @return Wrapped Entity
     */
    public static McEntity wrap(org.bukkit.entity.Entity bukkitEntity) {
        return wrap(((CraftEntity) bukkitEntity).getHandle());
    }

    private final Entity entity;

    McEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * Get the Bukkit version of this Entity
     *
     * @return Bukkit Entity
     */
    public org.bukkit.entity.Entity getBukkitEntity() {
        return this.entity.getBukkitEntity();
    }

    /**
     * Get the wrapped level of this entity
     *
     * @return Wrapped level of entity
     */
    public McLevel getMCLevel() {
        return McLevel.wrap(this.entity.level());
    }

    /**
     * Get the Entity ID of this entity
     *
     * @return ID of entity
     */
    public int getId() {
        return this.entity.getId();
    }

}
