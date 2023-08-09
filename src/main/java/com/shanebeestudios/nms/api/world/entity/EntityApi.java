package com.shanebeestudios.nms.api.world.entity;

import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.NamespacedKey;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        return ReflectionShortcuts.getNMSEntity(bukkitEntity);
    }

    /**
     * Damage an entity using a specified damage type
     *
     * @param victim    Victim receiving damage
     * @param damage    Amount of damage
     * @param damageKey NamespacedKey of damage type
     */
    public static void damage(@NotNull org.bukkit.entity.Entity victim, float damage, @Nullable NamespacedKey damageKey) {
        damage(victim, damage, damageKey, null, null, null);
    }

    /**
     * Damage an entity using a specified damage type
     *
     * @param victim    Victim receiving damage
     * @param damage    Amount of damage
     * @param damageKey NamespacedKey of damage type
     * @param vec       Location of damage
     */
    public static void damage(@NotNull org.bukkit.entity.Entity victim, float damage, @Nullable NamespacedKey damageKey, @Nullable Vector vec) {
        damage(victim, damage, damageKey, null, null, vec);
    }

    /**
     * Damage an entity using a specified damage type
     *
     * @param victim       Victim receiving damage
     * @param damage       Amount of damage
     * @param damageKey    NamespacedKey of damage type
     * @param directEntity Entity directly damaging entity
     */
    public static void damage(@NotNull org.bukkit.entity.Entity victim, float damage, NamespacedKey damageKey, @Nullable org.bukkit.entity.Entity directEntity) {
        damage(victim, damage, damageKey, directEntity, null, null);
    }

    /**
     * Damage an entity using a specified damage type
     *
     * @param victim        Victim receiving damage
     * @param damage        Amount of damage
     * @param damageKey     NamespacedKey of damage type
     * @param directEntity  Entity directly damaging entity
     * @param causingEntity Entity cause damage?!? I have no clue
     */
    public static void damage(@NotNull org.bukkit.entity.Entity victim, float damage, NamespacedKey damageKey, @Nullable org.bukkit.entity.Entity directEntity, @Nullable org.bukkit.entity.Entity causingEntity) {
        damage(victim, damage, damageKey, directEntity, causingEntity, null);
    }

    private static void damage(@NotNull org.bukkit.entity.Entity victim, float damage, @Nullable NamespacedKey damageKey, @Nullable org.bukkit.entity.Entity directEntity, @Nullable org.bukkit.entity.Entity causingEntity, @Nullable Vector vec) {
        Entity nmsEntity = getNMSEntity(victim);
        ServerLevel serverLevel = ReflectionShortcuts.getServerLevel(victim.getWorld());
        if (nmsEntity == null || serverLevel == null) return;
        if (damageKey == null) damageKey = NamespacedKey.minecraft("generic");

        Registry<DamageType> damageTypes = serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        ResourceLocation resourceLocation = new ResourceLocation(damageKey.getNamespace(), damageKey.getKey());
        ResourceKey<DamageType> damageTypeResourceKey = ResourceKey.create(Registries.DAMAGE_TYPE, resourceLocation);
        Holder.Reference<DamageType> damageType = damageTypes.getHolderOrThrow(damageTypeResourceKey);

        Entity entity1 = null;
        Entity entity2 = null;
        Vec3 vec3 = null;

        if (directEntity != null) entity1 = getNMSEntity(directEntity);
        if (causingEntity != null) entity2 = getNMSEntity(causingEntity);
        if (vec != null) vec3 = new Vec3(vec.getX(), vec.getY(), vec.getZ());

        DamageSource damageSource;
        if (entity1 != null) {
            if (entity2 != null) damageSource = new DamageSource(damageType, entity1, entity2);
            else damageSource = new DamageSource(damageType, entity1);
        } else if (vec3 != null) {
            damageSource = new DamageSource(damageType, vec3);
        } else {
            damageSource = new DamageSource(damageType);
        }
        nmsEntity.hurt(damageSource, damage);
    }

    /**
     * Get a list of all DamageTypes as {@link NamespacedKey NamespacedKeys}
     *
     * @return List of all DamageTypes
     */
    public static List<NamespacedKey> getDamageTypeKeys() {
        Registry<DamageType> damageTypes = MinecraftServer.getServer().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);

        List<NamespacedKey> keys = new ArrayList<>();
        damageTypes.keySet().forEach(resourceLocation -> {
            NamespacedKey namespacedKey = new NamespacedKey(resourceLocation.getNamespace(), resourceLocation.getPath());
            keys.add(namespacedKey);
        });
        return keys.stream().sorted(Comparator.comparing(NamespacedKey::toString)).collect(Collectors.toList());
    }

}
