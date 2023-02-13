package org.krystilize.skygod.utils;

import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;

public class BlockUtils {
    public static final Tag<Block> BLOCK_TO_PLACE = Tag.String("skygod:block_to_place")
            .map(Block::fromNamespaceId, block -> block.namespace().toString());
}
