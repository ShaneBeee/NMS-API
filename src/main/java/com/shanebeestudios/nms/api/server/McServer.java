package com.shanebeestudios.nms.api.server;

import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.Server;

import java.util.List;

/**
 * Wrapper for Minecraft (Dedicated) Server
 * <p>Provides easy-to-use mapped methods for Minecraft Server</p>
 */
@SuppressWarnings("unused")
public class McServer {

    /**
     * Wrap a Bukkit Server as MinecraftServer
     *
     * @param bukkitServer Bukkit Server to wrap
     * @return Wrapped MinecraftServer
     */
    public static McServer wrap(Server bukkitServer) {
        return new McServer(McUtils.getMinecraftServer(bukkitServer));
    }

    private final DedicatedServer server;

    private McServer(DedicatedServer server) {
        this.server = server;
    }

    /**
     * Check if the server is running
     *
     * @return True if server is running
     */
    public boolean isRunning() {
        return this.server.isRunning();
    }

    /**
     * Check if the server is ready
     *
     * @return True if server is ready
     */
    public boolean isReady() {
        return this.server.isReady();
    }

    /**
     * Check if the server is shutdown
     *
     * @return True if server is shutdown
     */
    public boolean isShutdown() {
        return this.server.isShutdown();
    }

    /**
     * Get the name of the server
     *
     * @return Name of server
     */
    public String getServerName() {
        return this.server.getServerName();
    }

    /**
     * Get the properties associated with the server
     *
     * @return Properties of server
     */
    public DedicatedServerProperties getProperties() {
        return this.server.getProperties();
    }

    /**
     * Get the Level ID name
     *
     * @return Level ID name
     */
    public String getLevelIdName() {
        return this.server.getLevelIdName();
    }

    /**
     * Get the server's player list
     *
     * @return Player list
     */
    public DedicatedPlayerList getPlayerList() {
        return this.server.getPlayerList();
    }

    /**
     * Get the status of the server
     *
     * @return Status of the server
     */
    public ServerStatus getServerStatus() {
        Iterable<ServerLevel> allLevels = this.server.getAllLevels();
        return this.server.getStatus();
    }

    /**
     * Get all server Levels
     *
     * @return All server levels
     */
    public List<ServerLevel> getAllLevels() {
        return (List<ServerLevel>) this.server.getAllLevels();
    }

}
