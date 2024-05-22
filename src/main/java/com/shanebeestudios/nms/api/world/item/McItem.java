package com.shanebeestudios.nms.api.world.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
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

}
