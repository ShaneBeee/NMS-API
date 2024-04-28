package com.shanebeestudios.nms.api.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for Minecraft Item
 * <p>Provides easy-to-use mapped methods for Items</p>
 */
@SuppressWarnings("unused")
public class McItem {

    /**
     * Wrap an Item for easy to use methods
     *
     * @param item Item to wrap
     * @return Wrapped item
     */
    public static McItem wrap(Item item) {
        return new McItem(item);
    }

    private final Item item;

    private McItem(Item item) {
        this.item = item;
    }

    /**
     * Get the Minecraft Item of this wrapper
     *
     * @return Minecraft Item
     */
    public Item getItem() {
        return this.item;
    }

    /**
     * Get the {@link McTier Tier} from a Tiered Item
     *
     * @return Tier of Item
     */
    @Nullable
    public McTier getTier() {
        if (this.item instanceof TieredItem tieredItem) return McTier.wrap(tieredItem);
        return null;
    }

    /**
     * Check if this item is edible
     *
     * @return whether this item is edible
     */
    public boolean isEdible() {
        return this.item.components().has(DataComponents.FOOD);
    }

    /**
     * Check if this item can always be eaten
     *
     * @return True if can always eat else false
     */
    public boolean canAlwaysEat() {
        FoodProperties foodProperties = this.item.components().get(DataComponents.FOOD);
        return foodProperties != null && foodProperties.canAlwaysEat();
    }

    /**
     * Get the nutrition if this is a food item
     *
     * @return Nutrition if food item else 0
     */
    public int getNutrition() {
        FoodProperties foodProperties = this.item.components().get(DataComponents.FOOD);
        return foodProperties != null ? foodProperties.nutrition() : 0;
    }

    /**
     * Get the saturation modifier if this is a food item
     *
     * @return Saturation modifier if food item else 0
     */
    public float getSaturationModifier() {
        FoodProperties foodProperties = this.item.components().get(DataComponents.FOOD);
        return foodProperties != null ? foodProperties.saturation() : 0;
    }

    /**
     * Check if this item is a meat product
     *
     * @return True if meat product else false
     * @deprecated No longer used in Minecraft
     */
    @Deprecated(forRemoval = true, since = "April 27/2024")
    public boolean isMeat() {
        return false;
    }

    /**
     * Check if this item can be instantly eaten
     *
     * @return True if can instantly eat else false
     * @deprecated No longer used in Minecraft
     */
    @Deprecated(forRemoval = true, since = "April 27/2024")
    public boolean isFastFood() {
        FoodProperties foodProperties = this.item.components().get(DataComponents.FOOD);
        if (foodProperties == null) return false;
        return foodProperties.eatSeconds() <= 0.8f;
    }

    /**
     * Check if this item is fire-resistant
     *
     * @return True if fire-resistant else false
     */
    public boolean isFireResistant() {
        return this.item.components().has(DataComponents.FIRE_RESISTANT);
    }

    /**
     * Check if Item is complex
     *
     * @return True if complex else false
     */
    public boolean isComplex() {
        return this.item.isComplex();
    }

    /**
     * Get the damage amount of this item
     * <p>Only works on Sword and Digger (axe, hoe, pickaxe, shovel) items</p>
     *
     * @return Damage amount if applicable else 0
     * @deprecated No longer used
     */
    @Deprecated(forRemoval = true, since = "April 27/2024")
    public float getDamage() {
        return 0;
    }

    /**
     * Get the Rarity of an Item
     * <p>Rarity returns different if the ItemStack is enchanted.
     * If you leave the ItemStack null, it may not give you the correct result.</p>
     *
     * @param itemStack ItemStack to check against (enchantments matter) (can be null)
     * @return Rarity of item
     */
    @Nullable
    public Rarity getRarity(@Nullable ItemStack itemStack) {
        net.minecraft.world.item.Rarity rarity = this.item.components().get(DataComponents.RARITY);
        if (rarity == null) return null;
        return switch (rarity) {
            case COMMON -> Rarity.COMMON;
            case UNCOMMON -> Rarity.UNCOMMON;
            case RARE -> Rarity.RARE;
            case EPIC -> Rarity.EPIC;
        };
    }

    /**
     * Item Rarity
     */
    public enum Rarity {
        /**
         *
         */
        COMMON,
        /**
         *
         */
        UNCOMMON,
        /**
         *
         */
        RARE,
        /**
         *
         */
        EPIC
    }

}
