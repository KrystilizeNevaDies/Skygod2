package org.krystilize.skygod;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.testing.Env;
import net.minestom.testing.EnvTest;
import org.junit.jupiter.api.Test;
import org.krystilize.skygod.block.Blocks;
import org.krystilize.skygod.crafting.CraftingRecipe;
import org.krystilize.skygod.item.Item;
import org.krystilize.skygod.loot.BlockLoot;
import org.krystilize.skygod.loot.Loot;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CraftingRecipesTests {

    @Test
    public void testLawOfConservation() {

        for (Block block : Blocks.values()) {
            int matter = block.getTag(Blocks.MATTER_TAG);

            assertFalse(matter < 0, "Block " + block + " has negative matter");


            // Blocklook
//            Loot.Generator blockLoot = BlockLoot.from(block);
//            for (Loot loot : blockLoot.generatePossible()) {
//                for (List<Item> lootResult : loot.generatePossible()) {
//                    int lootMatter = 0;
//                    for (Item item : lootResult) {
//                        lootMatter += item.matter();
//                    }
//                    int finalLootMatter = lootMatter;
//                    assertEquals(matter, lootMatter, () -> "Matter conservation failed for block " + block + ".\n" +
//                            " Block has " + matter + " matter, but loot result has " + finalLootMatter + " matter.\n" +
//                            " Loot result: " + lootResult);
//                }
//            }
        }

        // Crafting recipes
        for (CraftingRecipe recipe : CraftingRecipe.values()) {
            int totalMatter = 0;
            for (var entry : recipe.ingredients().values()) {
                Item item = entry.getValue();
                int count = entry.getKey();
                int itemMatter = item.matter();
                totalMatter += itemMatter * count;
            }
            Item result = recipe.result();
            int resultMatter = result.matter();

            int finalTotalMatter = totalMatter;
            assertEquals(totalMatter, resultMatter, () -> "Matter conservation failed for crafting recipe " + recipe.id() + ".\n" +
                    " Recipe has " + finalTotalMatter + " matter, but result has " + resultMatter + " matter.\n" +
                    " Recipe: " + recipe);
        }
    }
}
