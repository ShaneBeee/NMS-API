package com.shanebeestudios.nms.api.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.DamageResistant;

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
     * Check if this item is fire-resistant
     *
     * @return True if fire-resistant else false
     */
    public boolean isFireResistant() {
        if (this.item.components().has(DataComponents.DAMAGE_RESISTANT)) {
            DamageResistant data = this.item.components().get(DataComponents.DAMAGE_RESISTANT);
            return data != null && data.types() == DamageTypeTags.IS_FIRE;
        }
        return false;
    }

    /**
     * Check if Item is complex
     *
     * @return True if complex else false
     * @deprecated No longer a thing in MC
     */
    @Deprecated(since = "1.21.4")
    public boolean isComplex() {
        return false;
    }

}
