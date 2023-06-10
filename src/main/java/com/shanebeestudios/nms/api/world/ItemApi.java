package com.shanebeestudios.nms.api.world;

import com.shanebeestudios.nms.api.reflection.ReflectionShortcuts;
import com.shanebeestudios.nms.api.util.McUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bukkit.NamespacedKey;

/**
 * Api methods pertaining to a {@link org.bukkit.inventory.ItemStack}
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

}
