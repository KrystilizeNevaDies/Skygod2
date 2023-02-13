package org.krystilize.skygod.item;

import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;

public non-sealed interface Item extends ItemConstants {
    Tag<String> TAG_ID = Tag.String("skygod:item_id");

    ItemStack stack();

    String id();

    /**
     * The output when grinding this item.
     * @return The output when grinding this item.
     */
    @Nullable Item grindingOutput();

    TextColor color();

    /**
     * Returns the stateId of the item.
     * The stateId is the item offset in the item registry.
     *
     * @return the stateId of this item.
     */
    int stateId();

    /**
     * How much mass this item has.
     * @return How much mass this item has.
     */
    int matter();

    <T> Item withProperty(ItemProperty<T> property, T value);

    static @UnknownNullability Item from(@NotNull String name) {
        return ItemRegistry.REGISTRY.get(name);
    }

    static @Nullable Item fromItemStack(ItemStack itemStack) {
        String id = itemStack.getTag(TAG_ID);
        return id == null ? null : from(id);
    }

    static @NotNull Item fromStateId(int stateId) {
        return ItemRegistry.STATE_IDS.get(stateId);
    }

    static Collection<Item> values() {
        return ItemRegistry.REGISTRY.values();
    }
}
