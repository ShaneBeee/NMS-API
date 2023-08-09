package com.shanebeestudios.nms.api.world.item;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

/**
 * Wrapper class for Minecraft {@link Tier Tier}
 */
public class McTier {

    public static McTier wrap(TieredItem tieredItem) {
        return new McTier(tieredItem);
    }

    private final Tier tier;

    private McTier(TieredItem tieredItem) {
        this.tier = tieredItem.getTier();
    }

    public int getUses() {
        return this.tier.getUses();
    }

    public float getSpeed() {
        return this.tier.getSpeed();
    }

    public float getAttackDamageBonus() {
        return this.tier.getAttackDamageBonus();
    }

    public int getLevel() {
        return this.tier.getLevel();
    }

    public int getEnchantmentValue() {
        return this.tier.getEnchantmentValue();
    }

    public @NotNull Ingredient getRepairIngredient() {
        return this.tier.getRepairIngredient();
    }

}
