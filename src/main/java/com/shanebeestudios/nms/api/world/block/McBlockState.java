package com.shanebeestudios.nms.api.world.block;

import com.mojang.datafixers.util.Pair;
import com.shanebeestudios.nms.api.util.McUtils;
import com.shanebeestudios.nms.api.world.entity.EntityApi;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;

/**
 * Wrapper for Minecraft BlockState
 * <p>Provides easy-to-use mapped methods for BlockStates</p>
 * <p>Keep in mind, a Minecraft BlockState differs from a Bukkit BlockState.
 * A Minecraft BlockState is basically like Bukkit's {@link BlockData}</p>
 */
@SuppressWarnings("unused")
public class McBlockState {

    public static McBlockState wrap(BlockState blockState) {
        return new McBlockState(blockState);
    }

    public static McBlockState wrap(BlockData blockData) {
        BlockState blockState = McUtils.getBlockStateFromData(blockData);
        return wrap(blockState);
    }

    private final BlockState blockState;

    private McBlockState(BlockState blockState) {
        this.blockState = blockState;
    }

    /**
     * Get the Block of this BlockState
     *
     * @return Block of this BlockState
     */
    public Block getBlock() {
        return this.blockState.getBlock();
    }

    /**
     * Get a wrapped Block of this BlockState
     *
     * @return Wrapped Block of this BlockState
     */
    public McBlock getMcBlock() {
        return McBlock.wrap(this.blockState.getBlock());
    }

    /**
     * Check if this BlockState can survive at a location
     * <p>Example of this would be checking if sand is supported or a block can be placed over farmland/dirt path</p>
     *
     * @param location Location to check for survival
     * @return True if this BlockState can survive at this Location
     */
    public boolean canSurvive(Location location) {
        Pair<ServerLevel, BlockPos> levelPos = McUtils.getLevelPos(location);
        return this.blockState.canSurvive(levelPos.getFirst(), levelPos.getSecond());
    }

    /**
     * Check if an Entity can stand on this BlockState
     *
     * @param bukkitEntity Entity to check if it can stand on this BlockState
     * @return True if the Entity can stand on this BlockState
     */
    public boolean entityCanStandOn(Entity bukkitEntity) {
        net.minecraft.world.entity.Entity entity = EntityApi.getNMSEntity(bukkitEntity);
        Pair<ServerLevel, BlockPos> levelPos = McUtils.getLevelPos(bukkitEntity.getLocation());
        return this.blockState.entityCanStandOn(levelPos.getFirst(), levelPos.getSecond(), entity);
    }

    /**
     * Check if this BlockState can be replaced
     *
     * @return True if it can be replaced
     */
    public boolean canBeReplaced() {
        return this.blockState.canBeReplaced();
    }

    /**
     * Get the collision shape of this BlockState at a location in the world
     *
     * @param location Location to check for shape
     * @return VoxelShape of BlockState at location
     */
    public VoxelShape getCollisionShape(Location location) {
        Pair<ServerLevel, BlockPos> levelPos = McUtils.getLevelPos(location);
        return this.blockState.getCollisionShape(levelPos.getFirst(), levelPos.getSecond());
    }

    /**
     * Get the MapColor this BlockState will appear on a map
     *
     * @param location Location of BlockState to test for color
     * @return MapColor of BlockState at given Location
     */
    public MapColor getMapColor(Location location) {
        Pair<ServerLevel, BlockPos> levelPos = McUtils.getLevelPos(location);
        return this.blockState.getMapColor(levelPos.getFirst(), levelPos.getSecond());
    }

}
