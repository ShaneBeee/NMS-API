package com.shanebeestudios.nms.api.world.entity;

import com.mojang.authlib.GameProfile;
import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Api methods pertaining to a {@link org.bukkit.entity.Player}
 */
@SuppressWarnings("unused")
public class PlayerApi {

    private PlayerApi() {
    }

    private static final MinecraftServer MINECRAFT_SERVER = MinecraftServer.getServer();
    static final Map<String, FakePlayer> FAKE_PLAYERS = new HashMap<>();

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
        ServerLevel level = McUtils.getServerLevel(world);

        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        GameProfile gameProfile = new GameProfile(op.getUniqueId(), name);
        McUtils.setSkin(name, gameProfile);
        ServerPlayer serverPlayer = new ServerPlayer(MINECRAFT_SERVER, level, gameProfile, ClientInformation.createDefault());
        serverPlayer.setPos(loc.getX(), loc.getY(), loc.getZ());

        FakePlayer fakePlayer = new FakePlayer(serverPlayer);
        FAKE_PLAYERS.put(name, fakePlayer);
        fakePlayer.update();
        return fakePlayer;
    }

    /**
     * Spawn a {@link FakePlayer} async
     * <p>This will cache the fake player as well for later retrieval</p>
     *
     * @param name Name of fake player
     * @param loc  Location of fake player
     * @return FakePlayer instance
     */
    public static CompletableFuture<FakePlayer> spawnFakePlayerAsync(String name, Location loc) {
        World world = loc.getWorld() != null ? loc.getWorld() : Bukkit.getWorlds().get(0);
        ServerLevel level = McUtils.getServerLevel(world);

        CompletableFuture<FakePlayer> fakePlayerFuture = new CompletableFuture<>();
        CompletableFuture<GameProfile> skinFuture = CompletableFuture.supplyAsync(() -> {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            GameProfile gameProfile = new GameProfile(op.getUniqueId(), name);
            McUtils.setSkin(name, gameProfile);
            return gameProfile;
        });

        skinFuture.thenAccept(gameProfile -> {
            ServerPlayer serverPlayer = new ServerPlayer(MINECRAFT_SERVER, level, gameProfile, ClientInformation.createDefault());
            serverPlayer.setPos(loc.getX(), loc.getY(), loc.getZ());

            FakePlayer fakePlayer = new FakePlayer(serverPlayer);
            FAKE_PLAYERS.put(name, fakePlayer);
            fakePlayer.update();
            fakePlayerFuture.complete(fakePlayer);
        });

        return fakePlayerFuture;
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
     * Get all fake players
     *
     * @return List of all fake players
     */
    public static List<FakePlayer> getFakePlayers() {
        return (List<FakePlayer>) FAKE_PLAYERS.values();
    }

    /**
     * Get the Player's connection
     * <br>Useful for sending packets
     *
     * @param player Bukkit Player to get connection from
     * @return Connection from Player
     */
    public static @NotNull ServerGamePacketListenerImpl getPlayerConnection(@NotNull Player player) {
        ServerPlayer serverPlayer = ReflectionShortcuts.getNMSPlayer(player);
        return serverPlayer.connection;
    }

    /**
     * Send a Packet to a {@link Player Bukkit Player}
     *
     * @param player Player to send packet to
     * @param packet Packet to send
     */
    public static void sendPacket(@NotNull Player player, @NotNull Packet<?> packet) {
        ServerGamePacketListenerImpl playerConnection = getPlayerConnection(player);
        playerConnection.send(packet);
    }

    /**
     * Start the riptide animation for a player
     *
     * @param player Player to riptide
     * @param time   Ticks to riptide
     */
    public static void riptide(@NotNull Player player, int time) {
        net.minecraft.world.entity.player.Player nmsPlayer = ReflectionShortcuts.getNMSPlayer(player);
        nmsPlayer.startAutoSpinAttack(time);
    }

    /**
     * Make a player touch an entity
     * <p>This only works on a few entities, ex: touching a dropped item makes the player pick it up</p>
     *
     * @param player Player to do the touching
     * @param entity Entity to touch
     */
    public static void touch(@NotNull Player player, @NotNull Entity entity) {
        net.minecraft.world.entity.player.Player nmsPlayer = ReflectionShortcuts.getNMSPlayer(player);
        net.minecraft.world.entity.Entity nmsEntity = ReflectionShortcuts.getNMSEntity(entity);
        nmsEntity.playerTouch(nmsPlayer);
    }

}
