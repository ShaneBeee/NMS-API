package com.shanebeestudios.nms.api.world.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for Minecraft Item
 * <p>Provides easy-to-use mapped methods for Items</p>
 */
@SuppressWarnings("unused")
public class McItem {

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
        if (this.item instanceof TieredItem tieredItem) return new McTier(tieredItem);
        return null;
    }

    /**
     * Check if this item is edible
     *
     * @return whether this item is edible
     */
    public boolean isEdible() {
        return this.item.isEdible();
    }

    /**
     * Check if this item can always be eaten
     *
     * @return True if can always eat else false
     */
    public boolean canAlwaysEat() {
        FoodProperties foodProperties = this.item.getFoodProperties();
        return foodProperties != null && foodProperties.canAlwaysEat();
    }

    /**
     * Get the nutrition if this is a food item
     *
     * @return Nutrition if food item else 0
     */
    public int getNutrition() {
        FoodProperties foodProperties = this.item.getFoodProperties();
        return foodProperties != null ? foodProperties.getNutrition() : 0;
    }

    /**
     * Get the saturation modifier if this is a food item
     *
     * @return Saturation modifier if food item else 0
     */
    public float getSaturationModifier() {
        FoodProperties foodProperties = this.item.getFoodProperties();
        return foodProperties != null ? foodProperties.getSaturationModifier() : 0;
    }

    /**
     * Check if this item is a meat product
     *
     * @return True if meat product else false
     */
    public boolean isMeat() {
        FoodProperties foodProperties = this.item.getFoodProperties();
        return foodProperties != null && foodProperties.isMeat();
    }

    /**
     * Check if this item can be instantly eaten
     *
     * @return True if can instantly eat else false
     */
    public boolean isFastFood() {
        FoodProperties foodProperties = this.item.getFoodProperties();
        return foodProperties != null && foodProperties.isFastFood();
    }

    /**
     * Check if this item is fire-resistant
     *
     * @return True if fire-resistant else false
     */
    public boolean isFireResistant() {
        return this.item.isFireResistant();
    }

    public boolean isComplex() {
        return this.item.isComplex();
    }

    /**
     * Get the damage amount of this item
     * <p>Only works on Sword and Digger (axe, hoe, pickaxe, shovel) items</p>
     *
     * @return Damage amount if applicable else 0
     */
    public float getDamage() {
        if (this.item instanceof SwordItem swordItem) return swordItem.getDamage();
        if (this.item instanceof DiggerItem diggerItem) return diggerItem.getAttackDamage();
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
    public Rarity getRarity(@Nullable ItemStack itemStack) {
        net.minecraft.world.item.Rarity rarity = itemStack != null ? this.item.getRarity(itemStack) : this.item.rarity;
        return switch (rarity) {
            case COMMON -> Rarity.COMMON;
            case UNCOMMON -> Rarity.UNCOMMON;
            case RARE -> Rarity.RARE;
            case EPIC -> Rarity.EPIC;
        };
    }

    public enum Rarity {
        COMMON,
        UNCOMMON,
        RARE,
        EPIC
    }

}
