package com.shanebeestudios.nms.api.world;

import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for Minecraft Level
 * <p>Provides easy-to-use mapped methods for Levels</p>
 * <p>A Minecraft Level is the equivalent of a {@link World Bukkit World}</p>
 */
@SuppressWarnings("unused")
public class McLevel {

    /**
     * Wrap a Bukkit World
     *
     * @param world Bukkit World to wrap
     * @return Wrapped Minecraft Level
     */
    public static McLevel wrap(@NotNull World world) {
        ServerLevel serverLevel = McUtils.getServerLevel(world);
        return new McLevel(serverLevel);
    }

    /**
     * Wrap a Minecraft Level
     *
     * @param level Minecraft Level to wrap
     * @return Wrapper of Minecraft Level
     */
    public static McLevel wrap(@NotNull Level level) {
        return new McLevel(level);
    }

    private final Level level;

    private McLevel(Level level) {
        this.level = level;
    }

    /**
     * Get level of rain in a Level
     *
     * @return Level of rain
     */
    public float getRainLevel() {
        return this.level.rainLevel;
    }

    /**
     * Destroy a block at a location
     *
     * @param location       Location of block to destroy
     * @param drop           Whether the block will drop items
     * @param breakingEntity The entity that broke the block
     */
    public void destroyBlock(@NotNull Location location, boolean drop, @Nullable Entity breakingEntity) {
        BlockPos pos = McUtils.getPos(location);
        net.minecraft.world.entity.Entity entity = breakingEntity != null ? McUtils.getNMSEntity(breakingEntity) : null;
        this.level.destroyBlock(pos, drop, entity);
    }

    /**
     * Destroy a block at a location
     *
     * @param location Location of block to destroy
     * @param drop     Whether the block will drop items
     */
    public void destroyBlock(@NotNull Location location, boolean drop) {
        destroyBlock(location, drop, null);
    }

}
