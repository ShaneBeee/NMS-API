package com.shanebeestudios.nms.api.world.entity;

import com.mojang.authlib.GameProfile;
import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.GameType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;

/**
 * Represents a fake {@link ServerPlayer}
 * <p>
 * Can create in {@link PlayerApi#spawnFakePlayer(String, Location)}
 * </p>
 */
@SuppressWarnings("unused")
public class FakePlayer {

    private final ServerPlayer fakeServerPlayer;
    private final Entry fakePlayerEntry;
    private final int id;

    FakePlayer(ServerPlayer serverPlayer) {
        this.fakeServerPlayer = serverPlayer;
        this.fakePlayerEntry = new Entry(this.fakeServerPlayer.getUUID(), this.fakeServerPlayer.getGameProfile(), true, 0,
                GameType.CREATIVE, this.fakeServerPlayer.getDisplayName(), null);
        this.id = serverPlayer.getId();
    }

    /**
     * Teleport this player to another location
     * <p>Currently does not support changing worlds/levels.</p>
     *
     * @param location Location to teleport to
     */
    public void teleport(@NotNull Location location) {
        this.fakeServerPlayer.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        update();
    }

    private void update(ServerPlayer serverPlayer) {
        ServerGamePacketListenerImpl connection = serverPlayer.connection;
        connection.send(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER), this.fakePlayerEntry));
        connection.send(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED), this.fakePlayerEntry));
        connection.send(this.fakeServerPlayer.getAddEntityPacket());
    }

    /**
     * Update for a single Player
     *
     * @param player Player to update for
     */
    public void update(@NotNull Player player) {
        ServerPlayer serverPlayer = McUtils.getServerPlayer(player);
        update(serverPlayer);
    }

    /**
     * Update for all players online
     */
    public void update() {
        MinecraftServer.getServer().getPlayerList().players.forEach(this::update);
    }

    /**
     * Remove the player from the player list
     */
    public void removeFromPlayerList() {
        MinecraftServer.getServer().getPlayerList().players.forEach(serverPlayer -> {
            ServerGamePacketListenerImpl connection = serverPlayer.connection;
            connection.send(new ClientboundPlayerInfoRemovePacket(List.of(this.fakeServerPlayer.getUUID())));
        });
    }

    /**
     * Add the player to the player list
     */
    public void addToPlayerList() {
        MinecraftServer.getServer().getPlayerList().players.forEach(serverPlayer -> {
            ServerGamePacketListenerImpl connection = serverPlayer.connection;
            connection.send(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED), this.fakePlayerEntry));
        });
    }

    /**
     * Remove this fake player
     */
    public void remove() {
        PlayerApi.FAKE_PLAYERS.remove(this.fakeServerPlayer.getGameProfile().getName());
        MinecraftServer.getServer().getPlayerList().players.forEach(serverPlayer -> {
            ServerGamePacketListenerImpl connection = serverPlayer.connection;
            connection.send(new ClientboundRemoveEntitiesPacket(this.id));
            connection.send(new ClientboundPlayerInfoRemovePacket(List.of(this.fakeServerPlayer.getUUID())));
        });
    }

    /**
     * Get the Minecraft Player of this FakePlayer
     *
     * @return Minecraft Player
     */
    public net.minecraft.world.entity.player.Player getServerPlayer() {
        return this.fakeServerPlayer;
    }

    /**
     * Get the GameProfile of this FakePlayer
     *
     * @return GameProfile
     */
    public GameProfile getGameProfile() {
        return this.fakeServerPlayer.getGameProfile();
    }

    /**
     * Get the name of this FakePlayer
     *
     * @return Name
     */
    public String getName() {
        return this.fakeServerPlayer.getGameProfile().getName();
    }

    @Override
    public String toString() {
        return "FakePlayer{name='" + this.fakeServerPlayer.getGameProfile().getName() + "'}";
    }

}
