package com.shanebeestudios.nms.api.world.entity;

import com.mojang.authlib.GameProfile;
import com.shanebeestudios.nms.api.reflection.ReflectionUtils;
import com.shanebeestudios.nms.api.world.WorldApi;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Api methods pertaining to a {@link org.bukkit.entity.Player}
 */
@SuppressWarnings({"unused", "deprecation"})
public class PlayerApi {

    private static final MinecraftServer MINECRAFT_SERVER = MinecraftServer.getServer();
    private static final Map<String, FakePlayer> FAKE_PLAYERS = new HashMap<>();

    /**
     * Spawn a {@link FakePlayer}
     * <p>This will cache the fake player as well for later retrieval</p>
     *
     * @param name Name of fake player
     * @param loc  Location of fake player
     * @return FakePlayer instance
     */
    public static FakePlayer spawnFakePlayer(String name, Location loc) {
        World world = loc.getWorld() != null ? loc.getWorld() : Bukkit.getWorlds().get(0);
        ServerLevel level = WorldApi.getServerLevel(world);

        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        ServerPlayer serverPlayer = new ServerPlayer(MINECRAFT_SERVER, level, new GameProfile(op.getUniqueId(), name));
        serverPlayer.setPos(loc.getX(), loc.getY(), loc.getZ());

        FakePlayer fakePlayer = new FakePlayer(serverPlayer);
        FAKE_PLAYERS.put(name, fakePlayer);
        fakePlayer.update();
        return fakePlayer;
    }

    /**
     * Get a previously cached {@link FakePlayer}
     *
     * @param name Name of fake player
     * @return FakePlayer if cached otherwise null
     */
    @Nullable
    public static FakePlayer getFakePlayer(String name) {
        if (FAKE_PLAYERS.containsKey(name)) {
            return FAKE_PLAYERS.get(name);
        }
        return null;
    }

    /**
     * Get the Player's connection
     * <br>Useful for sending packets
     *
     * @param player Bukkit Player to get connection from
     * @return Connection from Player
     */
    @Nullable
    public static ServerGamePacketListenerImpl getPlayerConnection(@NotNull Player player) {
        Object nmsEntity = ReflectionUtils.getNMSEntity(player);
        if (nmsEntity instanceof ServerPlayer serverPlayer) {
            return serverPlayer.connection;
        }
        return null;
    }

    /**
     * Send a Packet to a {@link Player Bukkit Player}
     *
     * @param player Player to send packet to
     * @param packet Packet to send
     */
    public static void sendPacket(@NotNull Player player, @NotNull Packet<?> packet) {
        ServerGamePacketListenerImpl playerConnection = getPlayerConnection(player);
        if (playerConnection != null) {
            playerConnection.send(packet);
        }
    }

}
