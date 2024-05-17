package com.shanebeestudios.nms.api.world.entity;

import com.mojang.authlib.GameProfile;
import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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
        return spawnFakePlayer(name, loc, null);
    }

    /**
     * Spawn a {@link FakePlayer}
     * <p>This will cache the fake player as well for later retrieval</p>
     * <p>NOTE: The attached entity will spawn, and the player will take over its AI,
     * This may cause some client lag depending on the chosen entity.</p>
     * <p>Will automatically update to all players</p>
     *
     * @param name       Name of fake player
     * @param loc        Location of fake player
     * @param attachType Type of entity to spawn and attach the player to
     * @return FakePlayer instance
     */
    public static FakePlayer spawnFakePlayer(@NotNull String name, @NotNull Location loc, @Nullable EntityType attachType) {
        return spawnFakePlayer(name, loc, attachType, true);
    }

    /**
     * Spawn a {@link FakePlayer}
     * <p>This will cache the fake player as well for later retrieval</p>
     * <p>NOTE: The attached entity will spawn, and the player will take over its AI,
     * This may cause some client lag depending on the chosen entity.</p>
     * <p>If not using update, use {@link FakePlayer#update()} or {@link FakePlayer#update(Player)} to update to players.</p>
     *
     * @param name       Name of fake player
     * @param loc        Location of fake player
     * @param attachType Type of entity to spawn and attach the player to
     * @param update     Whether to update the fake player to all online players
     * @return FakePlayer instance
     */
    public static FakePlayer spawnFakePlayer(@NotNull String name, @NotNull Location loc, @Nullable EntityType attachType, boolean update) {
        // create player entity
        World world = loc.getWorld() != null ? loc.getWorld() : Bukkit.getWorlds().get(0);
        ServerLevel level = McUtils.getServerLevel(world);

        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        GameProfile gameProfile = new GameProfile(op.getUniqueId(), name);
        McUtils.setSkin(gameProfile);
        ServerPlayer serverPlayer = new ServerPlayer(MINECRAFT_SERVER, level, gameProfile, ClientInformation.createDefault());
        serverPlayer.setPos(loc.getX(), loc.getY(), loc.getZ());

        // Attempt attachment
        Entity attachedEntity = null;
        if (attachType != null) {
            Class<? extends org.bukkit.entity.Entity> entityClass = attachType.getEntityClass();
            assert entityClass != null;
            if (!LivingEntity.class.isAssignableFrom(entityClass)) {
                throw new IllegalArgumentException("Cannot use a non-living entity");
            }
            // Spawn entity used for attachment
            org.bukkit.entity.Entity spawn = loc.getWorld().spawn(loc, entityClass);
            attachedEntity = ReflectionShortcuts.getNMSEntity(spawn);

            // Visual remove that entity from the client
            ClientboundRemoveEntitiesPacket removePacket = new ClientboundRemoveEntitiesPacket(attachedEntity.getId());
            MinecraftServer.getServer().getPlayerList().players.forEach(player -> player.connection.send(removePacket));
        }

        // Create fake player and update to all clients
        FakePlayer fakePlayer = new FakePlayer(serverPlayer, attachedEntity);
        FAKE_PLAYERS.put(name, fakePlayer);
        if (update) fakePlayer.update();
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
        return spawnFakePlayerAsync(name, loc, null);
    }

    /**
     * Spawn a {@link FakePlayer} async
     * <p>This will cache the fake player as well for later retrieval</p>
     * <p>NOTE: The attached entity will spawn, and the player will take over its AI,
     * This may cause some client lag depending on the chosen entity.</p>
     * <p>Will automatically update to all players.</p>
     *
     * @param name       Name of fake player
     * @param loc        Location of fake player
     * @param attachType Type of entity to spawn and attach the player to
     * @return FakePlayer instance
     */
    public static CompletableFuture<FakePlayer> spawnFakePlayerAsync(@NotNull String name, @NotNull Location loc, @Nullable EntityType attachType) {
        return spawnFakePlayerAsync(name, loc, attachType, true);
    }

    /**
     * Spawn a {@link FakePlayer} async
     * <p>This will cache the fake player as well for later retrieval</p>
     * <p>NOTE: The attached entity will spawn, and the player will take over its AI,
     * This may cause some client lag depending on the chosen entity.</p>
     * <p>If not using update, use {@link FakePlayer#update()} or {@link FakePlayer#update(Player)} to update to players.</p>
     *
     * @param name       Name of fake player
     * @param loc        Location of fake player
     * @param attachType Type of entity to spawn and attach the player to
     * @param update     Whether to update the fake player to all online players
     * @return FakePlayer instance
     */
    public static CompletableFuture<FakePlayer> spawnFakePlayerAsync(@NotNull String name, @NotNull Location loc, @Nullable EntityType attachType, boolean update) {
        // create fake player
        World world = loc.getWorld() != null ? loc.getWorld() : Bukkit.getWorlds().get(0);
        ServerLevel level = McUtils.getServerLevel(world);

        // Spawn attachment if necessary
        Entity attachedEntity;
        if (attachType != null) {
            // Spawn entity used for attachment
            Class<? extends org.bukkit.entity.Entity> entityClass = attachType.getEntityClass();
            assert entityClass != null;
            if (!LivingEntity.class.isAssignableFrom(entityClass)) {
                throw new IllegalArgumentException("Cannot use a non-living entity");
            }

            org.bukkit.entity.Entity spawnedEntity = loc.getWorld().spawn(loc, entityClass);
            attachedEntity = ReflectionShortcuts.getNMSEntity(spawnedEntity);

            // Visually remove that entity from the client
            ClientboundRemoveEntitiesPacket removePacket = new ClientboundRemoveEntitiesPacket(attachedEntity.getId());
            MinecraftServer.getServer().getPlayerList().players.forEach(player -> player.connection.send(removePacket));
        } else {
            attachedEntity = null;
        }

        // Create Skin/GameProfile
        CompletableFuture<FakePlayer> fakePlayerFuture = new CompletableFuture<>();
        CompletableFuture<GameProfile> skinFuture = CompletableFuture.supplyAsync(() -> {
            OfflinePlayer op = Bukkit.getOfflinePlayer(name);
            GameProfile gameProfile = new GameProfile(op.getUniqueId(), name);
            McUtils.setSkin(gameProfile);
            return gameProfile;
        });

        // Finally create FakePlayer and apply skin
        return skinFuture.thenApply(gameProfile -> {
            ServerPlayer serverPlayer = new ServerPlayer(MINECRAFT_SERVER, level, gameProfile, ClientInformation.createDefault());
            serverPlayer.setPos(loc.getX(), loc.getY(), loc.getZ());

            FakePlayer fakePlayer = new FakePlayer(serverPlayer, attachedEntity);
            FAKE_PLAYERS.put(name, fakePlayer);
            fakePlayer.update();
            fakePlayerFuture.complete(fakePlayer);
            return fakePlayer;
        });
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

}
