package org.krystilize.skygod.block;

import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;

import java.util.HashMap;
import java.util.Map;

class BlocksRegistry {
    public static final Map<String, Block> REGISTRY;

    private static Block create(Block base, int matter, BlockHandler... handlers) {
        base = base.withTag(Blocks.MATTER_TAG, matter);
        if (handlers.length != 0) {
            base = base.withHandler(new ComponentBasedBlockHandler(handlers));
        }
        return base;
    }


    static {
        Map<String, Block> map = new HashMap<>();

        map.put("grass", create(Block.GRASS_BLOCK, 5, new GrassHandler()));
        map.put("tree_trunk", create(Block.OAK_LOG, 4));
        map.put("tree_leaves", create(Block.OAK_LEAVES, 1, new LeavesHandler()));
        map.put("dirt", create(Block.DIRT, 4));
        map.put("air", create(Block.AIR, 0));
        map.put("stone", create(Block.STONE, 4));
        map.put("grindstone", create(Block.GRINDSTONE, 1, new GrindstoneHandler()));
        map.put("workbench", create(Block.CRAFTING_TABLE, 1, new WorkbenchHandler()));

        REGISTRY = Map.copyOf(map);
    }
}
