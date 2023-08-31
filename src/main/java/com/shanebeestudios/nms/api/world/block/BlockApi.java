package com.shanebeestudios.nms.api.world.block;

import com.mojang.datafixers.util.Pair;
import com.shanebeestudios.nms.api.util.McUtils;
import com.shanebeestudios.nms.api.world.item.ItemApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * API methods relating to {@link Block Blocks}
 */
@SuppressWarnings("unused")
public class BlockApi {

    /**
     * Do not use
     */
    protected BlockApi() {
    }

    /**
     * Get the BlockData that would be placed at a specific
     * position as well as the location of the placement
     *
     * @param player      Player that would be placing
     * @param maxDistance Max distance to check for
     * @return Pair of location and block data
     */
    public static Pair<Location, BlockData> getForPlacement(Player player, int maxDistance) {
        ServerPlayer serverPlayer = McUtils.getServerPlayer(player);
        net.minecraft.world.item.ItemStack handItem = serverPlayer.getMainHandItem();
        if (handItem.getItem() instanceof BlockItem blockItem) {
            //pick range = (survival=4.5,creative=5), UNSURE = 1, fluid = false
            BlockHitResult blockHitResult = (BlockHitResult) serverPlayer.pick(maxDistance, 1, false);
            BlockPlaceContext blockPlaceContext = new BlockPlaceContext(serverPlayer, InteractionHand.MAIN_HAND, handItem, blockHitResult);
            BlockState stateForPlacement = blockItem.getBlock().getStateForPlacement(blockPlaceContext);
            if (stateForPlacement != null) {
                BlockData blockData = McUtils.getBlockDataFromState(stateForPlacement);
                Location location = McUtils.getLocation(blockPlaceContext.getClickedPos(), blockPlaceContext.getLevel());
                return Pair.of(location, blockData);
            }
        }
        return null;
    }

    /**
     * Get the BlockData that would be placed at a specific position
     *
     * @param player      Player that would be placing
     * @param maxDistance Max distance to check for
     * @return BlockData of what would be placed
     */
    @Nullable
    public static BlockData getBlockDataForPlacement(Player player, int maxDistance) {
        Pair<Location, BlockData> forPlacement = getForPlacement(player, maxDistance);
        if (forPlacement != null) return forPlacement.getSecond();
        return null;
    }

    /**
     * Get the BlockData that would be placed at a specific position
     *
     * @param player      Player that would be placing
     * @param hitBlock    The block that was hit
     * @param hitLocation The position of the player's cursor
     * @param face        The fact of the block relative to the hit block
     * @return BlockData of what would be placed
     */
    @Nullable
    public static BlockData getBlockDataForPlacement(Player player, Block hitBlock, Location hitLocation, BlockFace face) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        return getBlockDataForPlacement(player, hitBlock, hitLocation, face, handItem);
    }

    /**
     * Get the BlockData that would be placed at a specific position
     *
     * @param player      Player that would be placing
     * @param hitBlock    The block that was hit
     * @param hitLocation The position of the player's cursor
     * @param face        The fact of the block relative to the hit block
     * @param itemStack   ItemStack to try to place
     * @return BlockData of what would be placed
     */
    @Nullable
    public static BlockData getBlockDataForPlacement(Player player, Block hitBlock, Location hitLocation, BlockFace face, ItemStack itemStack) {
        ServerPlayer serverPlayer = McUtils.getServerPlayer(player);
        BlockPos hitTarget = McUtils.getPos(hitBlock.getLocation());
        net.minecraft.world.item.ItemStack nmsItemStack = ItemApi.getNMSItemStackCopy(itemStack);

        Vec3 hitPosition = McUtils.getVec3(hitLocation);
        Direction direction = McUtils.getDirection(face);

        return getBlockDataForPlacement(serverPlayer, hitTarget, hitPosition, direction, nmsItemStack);
    }

    /**
     * Get the BlockData that would be placed at a specific position
     *
     * @param serverPlayer Player that would be placing
     * @param hitTarget    The block that was hit
     * @param hitPosition  The position of the player's cursor
     * @param direction    Direction of the hit position relative to the hit block
     * @param itemStack    ItemStack to try to place
     * @return BlockData of what would be placed
     */
    @Nullable
    private static BlockData getBlockDataForPlacement(ServerPlayer serverPlayer, BlockPos hitTarget, Vec3 hitPosition, Direction direction, net.minecraft.world.item.ItemStack itemStack) {
        BlockHitResult hit = new BlockHitResult(hitPosition, direction, hitTarget, false);
        BlockPlaceContext blockPlaceContext = new BlockPlaceContext(serverPlayer, InteractionHand.MAIN_HAND, itemStack, hit);

        if (itemStack.getItem() instanceof BlockItem blockItem) {
            BlockState stateForPlacement = blockItem.getBlock().getStateForPlacement(blockPlaceContext);
            if (stateForPlacement != null) {
                return McUtils.getBlockDataFromState(stateForPlacement);
            }
        }
        return null;
    }

}
