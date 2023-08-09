package com.shanebeestudios.nms.api.world.item;

import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Api methods pertaining to an {@link org.bukkit.inventory.ItemStack}
 */
@SuppressWarnings("unused")
public class ItemApi {

    private static final Registry<Item> ITEM_REGISTRY = McUtils.getRegistry(Registries.ITEM);


    /**
     * Get a copy of a Minecraft ItemStack from a Bukkit ItemStack
     *
     * @param bukkitItemStack Bukkit ItemStack to convert
     * @return Minecraft ItemStack cloned from Bukkit ItemStack
     */
    public static ItemStack getNMSItemStackCopy(org.bukkit.inventory.ItemStack bukkitItemStack) {
        return ReflectionShortcuts.getNMSItemStackCopy(bukkitItemStack);
    }

    /**
     * Get a Minecraft ItemStack from a Bukkit ItemStack
     *
     * @param bukkitItemStack Bukkit ItemStack to convert
     * @return Minecraft ItemStack converted from Bukkit ItemStack
     */
    public static ItemStack getNMSItemStack(org.bukkit.inventory.ItemStack bukkitItemStack) {
        return ReflectionShortcuts.getNMSItemStack(bukkitItemStack);
    }

    public static NamespacedKey getKey(ItemStack itemStack) {
        ResourceLocation key = ITEM_REGISTRY.getKey(itemStack.getItem());
        assert key != null;
        return McUtils.getNamespacedKey(key);
    }

    /**
     * Get the Minecraft Item from a Minecraft ItemStack
     *
     * @param itemStack ItemStack to get Item from
     * @return Item from stack
     */
    public static Item getItem(ItemStack itemStack) {
        return itemStack.getItem();
    }

    /**
     * Get a Minecraft Item from a {@link Material Bukkit Material}
     *
     * @param bukkitMaterial Material to get Item from
     * @return Item from material if valid else AIR
     */
    @NotNull
    public static Item getItem(Material bukkitMaterial) {
        return ReflectionShortcuts.getItemFromMaterial(bukkitMaterial);
    }

    /**
     * Get the {@link McTier Tier} from a Tiered Item
     *
     * @param item Tiered Item to get Tier from
     * @return Tier of Item
     */
    @Nullable
    public static McTier getTier(Item item) {
        if (item instanceof TieredItem tieredItem) return new McTier(tieredItem);
        return null;
    }

    /**
     * Get a wrapped ItemStack for easy to use methods
     *
     * @param bukkitItemStack Bukkit ItemStack to wrap
     * @return Wrapped version of ItemStack
     */
    public static McItemStack getWrappedItemStack(org.bukkit.inventory.ItemStack bukkitItemStack) {
        ItemStack nmsItemStack = getNMSItemStack(bukkitItemStack);
        return McItemStack.wrap(nmsItemStack);
    }

    /**
     * Get a wrapped Item for easy to use methods
     *
     * @param item Item to wrap
     * @return Wrapped version of Item
     */
    public static McItem getWrappedItem(Item item) {
        return McItem.wrap(item);
    }

}
