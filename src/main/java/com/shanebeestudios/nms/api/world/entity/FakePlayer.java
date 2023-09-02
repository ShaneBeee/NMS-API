package com.shanebeestudios.nms.api.world.entity;

import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    private final int id;

    FakePlayer(ServerPlayer serverPlayer) {
        this.fakeServerPlayer = serverPlayer;
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
        connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this.fakeServerPlayer));
        connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this.fakeServerPlayer));
        connection.send(new ClientboundAddPlayerPacket(this.fakeServerPlayer));
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
            connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this.fakeServerPlayer));
            connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this.fakeServerPlayer));
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

    @Override
    public String toString() {
        return "FakePlayer{name='" + this.fakeServerPlayer.getGameProfile().getName() + "'}";
    }

}
