package org.krystilize.skygod.crafting;

import net.minestom.server.entity.Player;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.krystilize.skygod.item.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface CraftingManager {

    static int howManyTimesCanCraft(Player player, CraftingRecipe recipe) {
        Map<Item, Integer> items = new HashMap<>(itemsOfPlayer(player));

        int times = Integer.MAX_VALUE;
        for (Map.Entry<Item, Integer> ingredient : recipe.summativeIngredients().entrySet()) {
            int currentAmount = items.computeIfAbsent(ingredient.getKey(), k -> 0);
            int timesToCraft = currentAmount / ingredient.getValue();
            times = Math.min(times, timesToCraft);
        }
        return times;
    }

    private static Map<Item, Integer> itemsOfPlayer(Player player) {
        return Arrays.stream(player.getInventory().getItemStacks())
                .map(stack -> {
                    Item item = Item.fromItemStack(stack);
                    int amount = stack.amount();
                    return item == null ? null : Map.entry(item, amount);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.summingInt(Map.Entry::getValue)));
    }

    /**
     * Tries to craft the recipe.
     *
     * @param player the player who is crafting
     * @param recipe the recipe to craft
     * @param amount the amount of times to craft
     * @return true if the recipe was crafted, false otherwise.
     */
    static void craftRecipe(Player player, CraftingRecipe recipe, int amount) {
        PlayerInventory inventory = player.getInventory();
        for (Map.Entry<Integer, Item> ingredient : recipe.ingredients().values()) {
            UNSAFE_removeItems(player, ingredient.getValue(), ingredient.getKey() * amount);
        }
        inventory.addItemStack(recipe.result().stack().withAmount(amount));
    }

    /**
     * This method is UNSAFE
     *
     * @param player the player who is crafting
     * @param item   the item to add to the player's inventory
     * @param amount the amount of times to add the item
     * @return true if the item was added, false otherwise.
     */
    private static boolean UNSAFE_removeItems(Player player, Item item, int amount) {
        PlayerInventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack stack = inventory.getItemStack(slot);
            Item someItem = Item.fromItemStack(stack);
            if (someItem != item) {
                continue;
            }

            boolean willDepleteStack = amount >= stack.amount();
            if (willDepleteStack) {
                inventory.setItemStack(slot, ItemStack.AIR);
                amount -= stack.amount();
            } else {
                inventory.setItemStack(slot, stack.withAmount(stack.amount() - amount));
                return true;
            }
            if (amount <= 0) {
                return true;
            }
        }
        throw new IllegalArgumentException("Serious error, possible duplication/loss of items");
    }
}
