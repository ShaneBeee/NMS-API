package com.shanebeestudios.nms.api.world.entity;

import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;

/**
 * Represents a fake {@link ServerPlayer}
 */
@SuppressWarnings({"deprecation", "unused"})
public class FakePlayer {

    private final ServerPlayer serverPlayer;

    FakePlayer(ServerPlayer serverPlayer) {
        this.serverPlayer = serverPlayer;
    }

    /**
     * Teleport this player to another location
     * <p>Currently does not support changing worlds/levels.</p>
     *
     * @param location Location to teleport to
     */
    public void teleport(Location location) {
        this.serverPlayer.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        update();
    }

    void update() {
        for (ServerPlayer serverPlayer : MinecraftServer.getServer().getPlayerList().players) {
            ServerGamePacketListenerImpl connection = serverPlayer.connection;
            connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this.serverPlayer));
            connection.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, this.serverPlayer));
            connection.send(new ClientboundAddPlayerPacket(this.serverPlayer));
        }
    }

    private Vec3 vecFromLocation(Location location) {
        return new Vec3(location.getX(), location.getY(), location.getZ());
    }

}
