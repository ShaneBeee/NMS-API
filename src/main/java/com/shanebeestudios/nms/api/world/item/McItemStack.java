package com.shanebeestudios.nms.api.world.item;

import com.shanebeestudios.nms.api.util.McUtils;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for Minecraft ItemStack
 * <p>Provides easy-to-use mapped methods for ItemStacks</p>
 */
@SuppressWarnings("unused")
public class McItemStack {

    private final ItemStack itemStack;

    /**
     * Wrap an NMS ItemStack to use easy to use methods
     *
     * @param itemStack NMS ItemStack to wrap
     * @return Wrapped ItemStack
     */
    @NotNull
    public static McItemStack wrap(@NotNull ItemStack itemStack) {
        return new McItemStack(itemStack);
    }

    /**
     * Wrap a Bukkit ItemStack to use easy to use methods
     *
     * @param bukkitItemStack Bukkit ItemStack to wrap
     * @return Wrapped ItemStack
     */
    @NotNull
    public static McItemStack wrap(@NotNull org.bukkit.inventory.ItemStack bukkitItemStack) {
        return wrap(ItemApi.getNMSItemStack(bukkitItemStack));
    }

    /**
     * Get an empty ItemStack wrapped
     *
     * @return Wrapped empty ItemStack
     */
    @NotNull
    public static McItemStack empty() {
        return new McItemStack(ItemStack.EMPTY);
    }

    private McItemStack(@NotNull ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * Get Minecraft ItemStack of this wrapper
     *
     * @return ItemStack
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get a wrapped version of the Item of this ItemStack
     *
     * @return Wrapped Item
     */
    public McItem getItemWrapper() {
        return McItem.wrap(this.itemStack.getItem());
    }

    /**
     * Get the Item of this ItemStack
     *
     * @return Item of this ItemStack
     */
    public Item getItem() {
        return this.itemStack.getItem();
    }

    /**
     * Get the destroy speed of this item based on a specific {@link Block}
     *
     * @param bukkitBlock Block to test for speed
     * @return Speed based on block
     */
    public float getDestroySpeed(Block bukkitBlock) {
        BlockState blockState = McUtils.getBlockStateFromBlock(bukkitBlock);
        return this.itemStack.getDestroySpeed(blockState);
    }

    /**
     * Check if ItemStack is a damageable item
     *
     * @return True if damageable else false
     */
    public boolean isDamageableItem() {
        return this.itemStack.isDamageableItem();
    }

    /**
     * Get as a mirrored {@link org.bukkit.inventory.ItemStack Bukkit ItemStack}
     *
     * @return Mirrored Bukkit ItemStack
     */
    public org.bukkit.inventory.ItemStack getAsBukkitMirror() {
        return this.itemStack.asBukkitMirror();
    }

    /**
     * Get as a copied {@link org.bukkit.inventory.ItemStack Bukkit ItemStack}
     *
     * @return Copied Bukkit ItemStack
     */
    public org.bukkit.inventory.ItemStack getAsBukkitCopy() {
        return this.itemStack.asBukkitCopy();
    }

    /**
     * Get the lines of an ItemStack's tooltips as strings
     *
     * @param player   Player holding Item
     * @param advanced Whether to show advanced tooltips or not
     * @return List of strings for tooltips
     */
    public List<String> getTooltipLinesAsStrings(Player player, boolean advanced) {
        List<String> lines = new ArrayList<>();
        for (Component tooltipLine : getTooltipLines(player, advanced)) {
            String serialize = LegacyComponentSerializer.legacySection().serialize(tooltipLine);
            lines.add(serialize);
        }
        return lines;
    }

    /**
     * Get the lines of an ItemStack's tooltips as {@link Component Components}
     *
     * @param player   Player holding Item
     * @param advanced Whether to show advanced tooltips or not
     * @return List of Components for tooltips
     */
    public List<Component> getTooltipLines(Player player, boolean advanced) {
        List<Component> lines = new ArrayList<>();
        ServerPlayer serverPlayer = player != null ? McUtils.getServerPlayer(player) : null;
        List<net.minecraft.network.chat.Component> tooltipLines = this.itemStack.getTooltipLines(serverPlayer, advanced ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
        for (net.minecraft.network.chat.Component tooltipLine : tooltipLines) {
            Component adventure = PaperAdventure.asAdventure(tooltipLine);
            lines.add(adventure);
        }
        return lines;
    }

}
