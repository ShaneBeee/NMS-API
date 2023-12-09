package com.shanebeestudios.nms.api.world.block;

import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for Minecraft Block
 * <p>Provides easy-to-use mapped methods for Blocks</p>
 * <p>Keep in mind, a Minecraft Block differs from a Bukkit Block,
 * A Minecraft Block is actually a TYPE, not a reference to a block in the world.</p>
 */
@SuppressWarnings("unused")
public class McBlock {

    /**
     * Wrap a Minecraft Block as an McBlock
     *
     * @param block Minecraft block to wrap
     * @return Wrapper for Minecraft Block
     */
    public static McBlock wrap(Block block) {
        return new McBlock(block);
    }

    /**
     * Wrap a Bukkit Block as an McBlock
     * <p>Being different Block classes,
     * this essentially grabs the type of a Bukkit Block</p>
     *
     * @param bukkitBlock Bukkit Block to wrap
     * @return Wrapper for Minecraft Block
     */
    public static McBlock wrap(org.bukkit.block.Block bukkitBlock) {
        BlockState blockState = McUtils.getBlockStateFromBlock(bukkitBlock);
        Block block = blockState.getBlock();
        return wrap(block);
    }

    private final Block block;

    private McBlock(Block block) {
        this.block = block;
    }

    /**
     * Get the Item version of this Block
     *
     * @return Item version of this Block
     */
    @Nullable
    public Item asItem() {
        Item item = this.block.asItem();
        if (item == Items.AIR) return null;
        return item;
    }

    /**
     * Get the friction of this Block
     *
     * @return Friction of this block
     */
    public float getFriction() {
        return this.block.getFriction();
    }

    /**
     * Get the explosion resistance of this Block
     *
     * @return Explosion resistance of this Block
     */
    public float getExplosionResistance() {
        return this.block.getExplosionResistance();
    }

    /**
     * Get the jump factor of this Block
     *
     * @return Jump factor of this Block
     */
    public float getJumpFactor() {
        return this.block.getJumpFactor();
    }

    /**
     * Get the speed factor of this Block
     *
     * @return Speed factor of this Block
     */
    public float getSpeedFactor() {
        return this.block.getSpeedFactor();
    }

    /**
     * Check if this Block has collision
     *
     * @return True if Block has collision
     */
    public boolean hasCollision() {
        return this.block.hasCollision;
    }

    /**
     * Check if this Block is destroyable
     *
     * @return True if Block is destroyable
     */
    public boolean isDestroyable() {
        return this.block.isDestroyable();
    }

    /**
     * Check if this Block at a BlockState is randomly ticking
     *
     * @param bukkitBlock Bukkit Block to check if MC Block is randomly ticking
     * @return True if randomly ticking
     * @deprecated See Paper's new method {@link BlockData#isRandomlyTicked()}
     */
    @Deprecated(forRemoval = true,since = "Dec 9/2023")
    public boolean isRandomlyTicking(org.bukkit.block.Block bukkitBlock) {
        BlockState blockState = McUtils.getBlockStateFromBlock(bukkitBlock);
        return this.block.isRandomlyTicking(blockState);
    }

}
