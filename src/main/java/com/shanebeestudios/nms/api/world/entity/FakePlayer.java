package com.shanebeestudios.nms.api.world.entity;

import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private Entity attachedEntity;
    private final Entry fakePlayerEntry;
    private final int id;
    private McPlayer mcPlayer;
    private McEntity attachedMcEntity;

    FakePlayer(ServerPlayer serverPlayer) {
        this(serverPlayer, null);
    }

    FakePlayer(ServerPlayer serverPlayer, Entity attachedEntity) {
        if (attachedEntity != null) serverPlayer.setId(attachedEntity.getId());
        this.fakeServerPlayer = serverPlayer;
        this.fakePlayerEntry = new Entry(this.fakeServerPlayer.getUUID(), this.fakeServerPlayer.getGameProfile(), true, 0,
            GameType.CREATIVE, this.fakeServerPlayer.getDisplayName(), null);
        this.id = serverPlayer.getId();
        this.attachedEntity = attachedEntity;
    }

    /**
     * Get a wrapper Minecraft version of this FakePlayer
     *
     * @return Wrapper of FakePlayer
     */
    public McPlayer getMCPlayer() {
        if (this.mcPlayer == null) {
            this.mcPlayer = McPlayer.wrap(this.fakeServerPlayer);
        }
        return this.mcPlayer;
    }

    /**
     * Get the Entity this FakePlayer may be attached to
     * <p>May be null if this FakePlayer was never attached</p>
     *
     * @return Attached entity if present else null
     */
    @Nullable
    public Entity getAttachedEntity() {
        return this.attachedEntity;
    }

    /**
     * Get the Bukkit Entity this FakePlayer may be attached to
     * <p>May be null if this FakePlayer was never attached</p>
     *
     * @return Attached Bukkit Entity if present else null
     */
    public org.bukkit.entity.Entity getAttachedBukkitEntity() {
        if (this.attachedEntity != null) return this.attachedEntity.getBukkitEntity();
        return null;
    }

    /**
     * Get an McEntity this FakePlayer may be attached to
     * <p>May be null if this FakePlayer was never attached</p>
     *
     * @return Attached McEntity if present else null
     */
    @Nullable
    public McEntity getAttachedMcEntity() {
        if (this.attachedEntity != null) {
            if (this.attachedMcEntity == null) {
                this.attachedMcEntity = McEntity.wrap(this.attachedEntity);
            }
            return this.attachedMcEntity;
        }
        return null;
    }

    /**
     * Attach an entity to a fake player
     * <p>This will visually look like the player is doing what the entity would be doing</p>
     *
     * @param entity Entity to attach
     */
    public void attach(Entity entity) {
        if (this.attachedEntity != null) this.attachedEntity.discard();
        this.attachedEntity = entity;
        this.fakeServerPlayer.absMoveTo(entity.getX(), entity.getY(), entity.getZ());

        ClientboundRemoveEntitiesPacket removePacket = new ClientboundRemoveEntitiesPacket(
            entity.getId(), this.fakeServerPlayer.getId());
        MinecraftServer.getServer().getPlayerList().players.forEach(p -> p.connection.send(removePacket));
        this.fakeServerPlayer.setId(entity.getId());
        update();
    }

    /**
     * Attach an entity to a fake player
     * <p>This will visually look like the player is doing what the entity would be doing</p>
     *
     * @param bukkitEntity Entity to attach
     */
    public void attach(org.bukkit.entity.Entity bukkitEntity) {
        Entity nmsEntity = McUtils.getNMSEntity(bukkitEntity);
        attach(nmsEntity);
    }

    /**
     * Teleport this player to another location
     * <p>Currently does not support changing worlds/levels.</p>
     * <p>NOTE: If there is an attached entity, its best to just teleport that entity instead.</p>
     *
     * @param location Location to teleport to
     */
    public void teleport(@NotNull Location location) {
        Entity entityToMove = this.attachedEntity != null ? this.attachedEntity : this.fakeServerPlayer;
        entityToMove.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        if (this.attachedEntity == null) update();
    }

    /**
     * Move this player to a new location
     * <p>NOTE: Should be &lt;= 8 blocks, this is used for general player movement, not teleporting</p>
     * <p>NOTE: If there is an attached entity, just move/teleport that entity instead.</p>
     *
     * @param location Location to move player to
     */
    public void moveTo(@NotNull Location location) {
        Entity entityToMove = this.attachedEntity != null ? this.attachedEntity : this.fakeServerPlayer;
        entityToMove.absMoveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());

        // If attached entity is found, don't send packets
        if (this.attachedEntity != null) return;
        double oldX = this.fakeServerPlayer.getX();
        double oldY = this.fakeServerPlayer.getY();
        double oldZ = this.fakeServerPlayer.getZ();

        Vec3 vec3 = new Vec3(location.getX() - oldX, location.getY() - oldY, location.getZ() - oldZ);
        short x = (short) this.fakeServerPlayer.getPositionCodec().encodeX(vec3);
        short y = (short) this.fakeServerPlayer.getPositionCodec().encodeY(vec3);
        short z = (short) this.fakeServerPlayer.getPositionCodec().encodeZ(vec3);
        byte yaw = (byte) Mth.floor(this.fakeServerPlayer.getYRot() * 256.0F / 360.0F);
        byte pitch = (byte) Mth.floor(this.fakeServerPlayer.getXRot() * 256.0F / 360.0F);

        ClientboundMoveEntityPacket.PosRot positionPacket = new ClientboundMoveEntityPacket.PosRot(
            this.id, x, y, z, yaw, pitch, true);
        MinecraftServer.getServer().getPlayerList().players.forEach(p -> p.connection.send(positionPacket));

        byte headRot = (byte) Mth.floor(this.fakeServerPlayer.getYHeadRot() * 256.0F / 360.0F);
        ClientboundRotateHeadPacket rotateHeadPacket = new ClientboundRotateHeadPacket(this.fakeServerPlayer, headRot);
        MinecraftServer.getServer().getPlayerList().players.forEach(p -> p.connection.send(rotateHeadPacket));
    }

    private void update(ServerPlayer serverPlayer) {
        ServerGamePacketListenerImpl connection = serverPlayer.connection;
        connection.send(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER), this.fakePlayerEntry));
        connection.send(new ClientboundPlayerInfoUpdatePacket(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED), this.fakePlayerEntry));
        assert serverPlayer.tracker != null;
        connection.send(this.fakeServerPlayer.getAddEntityPacket(serverPlayer.tracker.serverEntity));
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
        if (this.attachedEntity != null) this.attachedEntity.discard();
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

    @Override
    public String toString() {
        String attach = this.attachedEntity != null ? (",attached=" + this.attachedEntity.getType().toShortString()) : "";
        return "FakePlayer{name='" + this.fakeServerPlayer.getGameProfile().getName() + "'" + attach + "}";
    }

}
