package com.shanebeestudios.nms.api.world.biome;

import com.shanebeestudios.nms.api.util.McUtils;
import com.shanebeestudios.nms.api.util.ReflectionUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;

import java.util.IdentityHashMap;

/**
 * Create and register a new Biome
 */
@SuppressWarnings("unused")
public class BiomeDefinition {

    private final ResourceLocation key;
    private final Biome.BiomeBuilder biomeBuilder;
    private BiomeSpecialEffects.Builder specialEffects = null;

    public BiomeDefinition(NamespacedKey key) {
        this.key = McUtils.getResourceLocation(key);
        this.biomeBuilder = new Biome.BiomeBuilder();
    }

    public BiomeDefinition temperature(float temperature) {
        this.biomeBuilder.temperature(temperature);
        return this;
    }

    public BiomeDefinition downfall(float downfall) {
        this.biomeBuilder.downfall(downfall);
        return this;
    }

    public BiomeDefinition hasPrecipitation(boolean hasPrecipitation) {
        this.biomeBuilder.hasPrecipitation(hasPrecipitation);
        return this;
    }

    // SPECIAL EFFECTS
    public BiomeDefinition fogColor(Color fogColor) {
        checkAndCreateSpecialEffects();
        this.specialEffects.fogColor(fogColor.asRGB());
        return this;
    }

    public BiomeDefinition waterColor(Color waterColor) {
        checkAndCreateSpecialEffects();
        this.specialEffects.waterColor(waterColor.asRGB());
        return this;
    }

    public BiomeDefinition waterFogColor(Color waterFogColor) {
        checkAndCreateSpecialEffects();
        this.specialEffects.waterFogColor(waterFogColor.asRGB());
        return this;
    }

    public BiomeDefinition skyColor(Color skyColor) {
        checkAndCreateSpecialEffects();
        this.specialEffects.skyColor(skyColor.asRGB());
        return this;
    }

    public BiomeDefinition foliageColorOverride(Color foliageColor) {
        checkAndCreateSpecialEffects();
        this.specialEffects.foliageColorOverride(foliageColor.asRGB());
        return this;
    }

    public BiomeDefinition grassColorOverride(Color grassColor) {
        checkAndCreateSpecialEffects();
        this.specialEffects.grassColorOverride(grassColor.asRGB());
        return this;
    }

    public BiomeDefinition grassColorModifier(GrassModifier grassModifier) {
        checkAndCreateSpecialEffects();
        this.specialEffects.grassColorModifier(grassModifier.getModifier());
        return this;
    }

    private void checkAndCreateSpecialEffects() {
        if (this.specialEffects == null) {
            this.specialEffects = new BiomeSpecialEffects.Builder();
        }
    }

    public Biome register() {
        if (this.specialEffects != null) {
            this.biomeBuilder.specialEffects(this.specialEffects.build());
        } else {
            this.biomeBuilder.specialEffects(new BiomeSpecialEffects.Builder()
                .fogColor(12638463)
                .skyColor(7907327)
                .waterColor(4159204)
                .waterFogColor(329011)
                .build());
        }
        this.biomeBuilder.generationSettings(new BiomeGenerationSettings.PlainBuilder().build());
        this.biomeBuilder.mobSpawnSettings(new MobSpawnSettings.Builder().build());

        Biome biome = this.biomeBuilder.build();
        DedicatedServer minecraftServer = McUtils.getMinecraftServer(Bukkit.getServer());
        RegistryAccess.Frozen registryAccess = minecraftServer.registryAccess();
        Registry<Biome> biomeRegistry = registryAccess.registry(Registries.BIOME).orElseThrow();

        ReflectionUtils.setField("frozen", biomeRegistry, false);
        ReflectionUtils.setField("unregisteredIntrusiveHolders", biomeRegistry, new IdentityHashMap<>());

        Holder.Reference<Biome> holder = biomeRegistry.createIntrusiveHolder(biome);
        ResourceKey<Biome> resourceKey = ResourceKey.create(Registries.BIOME, this.key);
        Registry.register(biomeRegistry, resourceKey, holder.value());
        biomeRegistry.freeze();
        return biome;
    }

    public enum GrassModifier {
        NONE(BiomeSpecialEffects.GrassColorModifier.NONE),
        DARK_FOREST(BiomeSpecialEffects.GrassColorModifier.DARK_FOREST),
        SWAMP(BiomeSpecialEffects.GrassColorModifier.SWAMP);

        private final BiomeSpecialEffects.GrassColorModifier modifier;

        GrassModifier(BiomeSpecialEffects.GrassColorModifier modifier) {
            this.modifier = modifier;
        }

        public BiomeSpecialEffects.GrassColorModifier getModifier() {
            return this.modifier;
        }
    }

}
