package com.shanebeestudios.nms.api.world.entity;

import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a fake {@link ServerPlayer}
 * <p>
 *     Can create in {@link PlayerApi#spawnFakePlayer(String, Location)}
 * </p>
 */
@SuppressWarnings({"deprecation", "unused"})
public class FakePlayer {

    private final ServerPlayer fakeServerPlayer;

    FakePlayer(ServerPlayer serverPlayer) {
        this.fakeServerPlayer = serverPlayer;
    }

    /**
     * Teleport this player to another location
     * <p>Currently does not support changing worlds/levels.</p>
     *
     * @param location Location to teleport to
     */
    public void teleport(Location location) {
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
    public void update(Player player) {
        ServerPlayer serverPlayer = McUtils.getServerPlayer(player);
        update(serverPlayer);
    }

    /**
     * Update for all players online
     */
    public void update() {
        MinecraftServer.getServer().getPlayerList().players.forEach(this::update);
    }

    private Vec3 vecFromLocation(Location location) {
        return new Vec3(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public String toString() {
        return "FakePlayer{name='" + this.fakeServerPlayer.getGameProfile().getName() + "'}";
    }

}
