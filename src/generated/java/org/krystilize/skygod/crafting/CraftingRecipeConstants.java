package org.krystilize.skygod.crafting;

sealed interface CraftingRecipeConstants permits CraftingRecipe {
    CraftingRecipe LEAVES_AND_DIRT_TO_GRASS = CraftingRecipe.from("leaves_and_dirt_to_grass");
    CraftingRecipe STICKS_AND_PEBBLES_TO_PICKAXE = CraftingRecipe.from("sticks_and_pebbles_to_pickaxe");
}
