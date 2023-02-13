package org.krystilize.skygod.block;

import net.minestom.server.instance.block.Block;

sealed interface BlockConstants permits Blocks {
    Block GRASS_BLOCK = Blocks.from("grass");
    Block TREE_TRUNK = Blocks.from("tree_trunk");
    Block TREE_LEAVES = Blocks.from("tree_leaves");
    Block DIRT = Blocks.from("dirt");
    Block AIR = Blocks.from("air");
    Block STONE = Blocks.from("stone");
    Block GRINDSTONE = Blocks.from("grindstone");
    Block WORKBENCH = Blocks.from("workbench");
}
