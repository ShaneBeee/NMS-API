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

    // TODO come back to this later
//    public static void spawnFakeVillager(String name, Location loc) {
//        Villager bukkitVillager = loc.getWorld().spawn(loc, Villager.class);
//        int villagerID = bukkitVillager.getEntityId();
//        ClientboundRemoveEntitiesPacket removePacket = new ClientboundRemoveEntitiesPacket(villagerID);
//        MinecraftServer.getServer().getPlayerList().players.forEach(p -> p.connection.send(removePacket));
//
//        World world = loc.getWorld() != null ? loc.getWorld() : Bukkit.getWorlds().get(0);
//        ServerLevel level = McUtils.getServerLevel(world);
//
//        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
//        GameProfile gameProfile = new GameProfile(op.getUniqueId(), name);
//        McUtils.setSkin(name, gameProfile);
//        ServerPlayer serverPlayer = new ServerPlayer(MINECRAFT_SERVER, level, gameProfile, ClientInformation.createDefault());
//        serverPlayer.setPos(loc.getX(), loc.getY(), loc.getZ());
//        serverPlayer.setId(villagerID);
//
//        ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(serverPlayer.getUUID(), serverPlayer.getGameProfile(), true, 0,
//                GameType.CREATIVE, serverPlayer.getDisplayName(), null);
//
//        MinecraftServer.getServer().getPlayerList().players.forEach(player -> {
//            ServerGamePacketListenerImpl connection = player.connection;
//            connection.send(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER), entry));
//            connection.send(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED), entry));
//            connection.send(serverPlayer.getAddEntityPacket());
//        });
//    }

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

}
