package org.krystilize.skygod.crafting;

import org.krystilize.skygod.item.Item;
import org.krystilize.skygod.utils.CachedObject;

import java.util.Map;
import java.util.stream.Collectors;

public record CraftingRecipeImpl(String id, CachedObject<Item> cachedResult,
                                 CachedObject<Map<Integer, Map.Entry<Integer, Item>>> ingredientsCache,
                                 CachedObject<Map<Item, Integer>> summativeIngredientsCache) implements CraftingRecipe {

    public CraftingRecipeImpl(String id, String result, Map<Integer, Map.Entry<Integer, String>> ingredients,
                              Map<String, Integer> summativeIngredients) {
        this(id, CachedObject.from(() -> Item.from(result)),
                CachedObject.from(() -> ingredients.entrySet()
                        .stream()
                        .map(entry -> {
                            String itemId = entry.getValue().getValue();
                            Item item = Item.from(itemId);
                            return Map.entry(entry.getKey(), Map.entry(entry.getValue().getKey(), item));
                        })
                        .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue))),
                CachedObject.from(() -> {
                    Map<Item, Integer> map;

                    try {
                        map = summativeIngredients.entrySet()
                                .stream()
                                .map(entry -> {
                                    String itemId = entry.getKey();
                                    Item item = Item.from(itemId);
                                    return Map.entry(item, entry.getValue());
                                })
                                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
                        // TODO: Debug why this happens
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid recipe: " + id, e);
                    }

                    return map;
                })
        );
    }
    @Override
    public Item result() {
        return cachedResult.get();
    }

    @Override
    public Map<Integer, Map.Entry<Integer, Item>> ingredients() {
        return ingredientsCache.get();
    }

    @Override
    public Map<Item, Integer> summativeIngredients() {
        return summativeIngredientsCache.get();
    }
}
