package com.shanebeestudios.nms.api.world.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for Minecraft {@link Tier Tier}
 */
public class McTier {

    /**
     * Wrapper an NMS TieredItem for easy to use methods
     *
     * @param tieredItem NMS TieredItem to wrap
     * @return Wrapped item
     */
    public static McTier wrap(TieredItem tieredItem) {
        return new McTier(tieredItem);
    }

    private final Tier tier;

    private McTier(TieredItem tieredItem) {
        this.tier = tieredItem.getTier();
    }

    /**
     * Get the uses of this tier
     *
     * @return Uses of this tier
     */
    public int getUses() {
        return this.tier.getUses();
    }

    /**
     * Get the speed of this tier
     *
     * @return Speed of this tier
     */
    public float getSpeed() {
        return this.tier.getSpeed();
    }

    /**
     * Get the attack damage bonus of this tier
     *
     * @return Attack damage bonus of this tier
     */
    public float getAttackDamageBonus() {
        return this.tier.getAttackDamageBonus();
    }

    /**
     * Get the level of this tier
     *
     * @return Level of this tier
     */
    public int getLevel() {
        return this.tier.getLevel();
    }

    /**
     * Get the enchantment value of this tier
     *
     * @return Enchantment value of this tier
     */
    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    /**
     * Get the repair ingredient of this tier
     *
     * @return Repair ingredient of this tier
     */
    public @NotNull Ingredient getRepairIngredient() {
        return this.tier.getRepairIngredient();
    }

}
