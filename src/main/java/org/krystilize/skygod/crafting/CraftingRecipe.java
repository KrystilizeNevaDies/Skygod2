package org.krystilize.skygod.crafting;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.krystilize.skygod.item.Item;

import java.util.Collection;
import java.util.Map;

public non-sealed interface CraftingRecipe extends CraftingRecipeConstants {
    Item result();

    /**
     * @return the ingredients. Key is the position, value entry's key is the amount, value entry's value is the item.
     */
    Map<Integer, Map.Entry<Integer, Item>> ingredients();

    Map<Item, Integer> summativeIngredients();

    String id();

    // TODO: Registry system for crafting recipes

    static @UnknownNullability CraftingRecipe from(String id) {
        return CraftingRecipeRegistry.REGISTRY.get(id);
    }

    static @NotNull Collection<CraftingRecipe> values() {
        return CraftingRecipeRegistry.REGISTRY.values();
    }
}
