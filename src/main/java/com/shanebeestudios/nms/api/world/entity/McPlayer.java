package com.shanebeestudios.nms.api.world.entity;

import com.mojang.authlib.GameProfile;
import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.entity.CraftEntity;

/**
 * Wrapper for Minecraft Player
 */
@SuppressWarnings("unused")
public class McPlayer extends McEntity {

    /**
     * Wrap a Minecraft Player
     *
     * @param nmsPlayer Minecraft Player to wrap
     * @return Wrapped player
     */
    public static McPlayer wrap(Player nmsPlayer) {
        return new McPlayer(nmsPlayer);
    }

    /**
     * Wrap a Bukkit Player
     *
     * @param bukkitPlayer Bukkit Player to wrap
     * @return Wrapped player
     */
    public static McPlayer wrap(org.bukkit.entity.Player bukkitPlayer) {
        ServerPlayer serverPlayer = McUtils.getServerPlayer(bukkitPlayer);
        return wrap(serverPlayer);
    }

    private final Player player;

    private McPlayer(Player player) {
        super(player);
        this.player = player;
    }

    /**
     * Get the GameProfile belonging to this player
     *
     * @return GameProfile of player
     */
    public GameProfile getGameProfile() {
        return this.player.getGameProfile();
    }

    /**
     * Get the name of this player
     *
     * @return Name of player
     */
    public String getName() {
        return getGameProfile().getName();
    }

    /**
     * Start the riptide animation for a player
     *
     * @param time Ticks to riptide
     */
    public void riptide(int time) {
        this.player.startAutoSpinAttack(time);
    }

    /**
     * Make a player touch an entity
     * <p>This only works on a few entities, ex: touching a dropped item makes the player pick it up</p>
     *
     * @param bukkitEntity Entity to touch
     */
    public void touch(org.bukkit.entity.Entity bukkitEntity) {
        Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
        nmsEntity.playerTouch(this.player);
    }

}
