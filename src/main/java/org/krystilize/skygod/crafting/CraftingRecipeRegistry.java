package org.krystilize.skygod.crafting;

import com.moandjiezana.toml.Toml;
import org.krystilize.skygod.item.Item;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class CraftingRecipeRegistry {

    static final Map<String, CraftingRecipe> REGISTRY;

    private static Builder builder(String result) {
        return new Builder(result);
    }

    private static class Builder {

        private final String result;
        private Map<Integer, Map.Entry<Integer, String>> ingredients = new HashMap<>();

        public Builder(String result) {
            this.result = result;
        }

        public Builder ingredient(int position, int amount, String item) {
            ingredients.put(position, Map.entry(amount, item));
            return this;
        }

        public CraftingRecipe build(String id) {
            ingredients = Map.copyOf(ingredients);
            Map<String, Integer> summativeIngredients = ingredients.values()
                    .stream()
                    .collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.summingInt(Map.Entry::getKey)));
            return new CraftingRecipeImpl(id, result, ingredients, summativeIngredients);
        }
    }

    static {
        Map<String, CraftingRecipe> map = new HashMap<>();

        Toml toml = new Toml().read(new File("craftingrecipes.toml"));

        for (Map.Entry<String, Object> entry : toml.entrySet()) {

            // Read all the fields
            String id = entry.getKey();
            Toml data = (Toml) entry.getValue();
            Toml items = Objects.requireNonNullElse(data.getTable("items"), new Toml());
            List<List<String>> recipe = Objects.requireNonNull(data.getList("recipe"), "Recipe is null");
            String result = Objects.requireNonNull(data.getString("result"), "Result is null");

            // Fill the item map
            Map<String, Map.Entry<String, Integer>> itemsMap = new HashMap<>();
            for (Map.Entry<String, Object> itemEntry : items.entrySet()) {
                String itemKey = Objects.requireNonNull(itemEntry.getKey(), "Item key is null");
                Toml itemData = Objects.requireNonNullElse(items.getTable(itemKey), new Toml());
                String itemId = Objects.requireNonNull(itemData.getString("id"), "Item id is null");
                Number itemNumber = Objects.requireNonNull(itemData.getLong("amount"), "Item amount is null");
                itemsMap.put(itemKey, Map.entry(itemId, itemNumber.intValue()));
            }

            var builder = builder(result);
            int slot = -1;
            for (List<String> row : recipe) {
                for (String value : row) {
                    slot++;
                    if ("".equals(value)) {
                        continue;
                    }
                    var itemEntry = itemsMap.get(value);
                    Objects.requireNonNull(itemEntry, "Item entry is null");
                    String itemKey = itemEntry.getKey();
                    int amount = itemsMap.get(value).getValue();
                    builder.ingredient(slot, amount, itemKey);
                }
            }

            map.put(id, builder.build(id));
        }

        REGISTRY = Map.copyOf(map);
    }
}
