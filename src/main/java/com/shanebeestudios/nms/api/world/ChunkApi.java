package com.shanebeestudios.nms.api.world;

import com.shanebeestudios.nms.api.reflection.ReflectionConstants;
import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Api methods pertaining to a {@link Chunk}
 */
@SuppressWarnings("unused")
public class ChunkApi {

    private ChunkApi() {
    }

    // LevelChunk#isTicking is package private
    private static final Method CHUNK_IS_TICKING_METHOD;

    static {
        try {
            CHUNK_IS_TICKING_METHOD = LevelChunk.class.getDeclaredMethod(ReflectionConstants.LEVEL_CHUNK_IS_TICKING_METHOD, BlockPos.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        CHUNK_IS_TICKING_METHOD.setAccessible(true);
    }

    /**
     * Get a Minecraft LevelChunk from a {@link Chunk Bukkit Chunk}
     *
     * @param chunk Bukkit chunk
     * @return Minecraft LevelChunk from Bukkit chunk
     */
    @NotNull
    public static LevelChunk getLevelChunk(@NotNull Chunk chunk) {
        return ReflectionShortcuts.getLevelChunk(chunk);
    }

    /**
     * Get the ticket holders of a chunk
     * <p>This represents the players that are holding a chunk open</p>
     *
     * @param chunk Chunk to grab ticket holders from
     * @return List of players holding the chunk open
     */
    @NotNull
    public static List<Player> getTicketHolders(@NotNull Chunk chunk) {
        LevelChunk levelChunk = getLevelChunk(chunk);
        ServerLevel level = (ServerLevel) levelChunk.getLevel();
        ChunkMap chunkMap = level.getChunkSource().chunkMap;

        List<Player> players = new ArrayList<>();
        chunkMap.getPlayers(levelChunk.getPos(), false)
                .forEach(serverPlayer -> players.add(serverPlayer.getBukkitEntity()));
        return players;
    }

    /**
     * Check if a {@link Chunk} is ticking at a {@link Location}
     *
     * @param location Location to check for ticking
     * @return True if chunk is ticking else false
     * @deprecated Instead use {@link Chunk#getLoadLevel()}
     */
    @Deprecated(forRemoval = true, since = "1.4.0")
    public static boolean isTickingAtLocation(@NotNull Location location) {
        LevelChunk levelChunk = getLevelChunk(location.getChunk());
        BlockPos pos = McUtils.getPos(location);
        try {
            return (boolean) CHUNK_IS_TICKING_METHOD.invoke(levelChunk, pos);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if a {@link Chunk} is ticking
     * <p>Similar to {@link #isTickingAtLocation(Location)} but from the middle of the chunk</p>
     *
     * @param chunk Chunk to check if ticking
     * @return True if chunk is ticking else false
     * @deprecated Instead use {@link Chunk#getLoadLevel()}
     */
    @Deprecated(forRemoval = true, since = "1.4.0")
    public static boolean isTicking(Chunk chunk) {
        World world = chunk.getWorld();
        int x = (chunk.getX() << 4) + 7;
        int z = (chunk.getZ() << 4) + 7;
        Location location = new Location(world, x, 1, z);
        return isTickingAtLocation(location);
    }

}
