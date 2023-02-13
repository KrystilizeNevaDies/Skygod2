package org.krystilize.skygod.loot;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;

public interface BlockLoot {
    static Loot.@NotNull Generator from(Block block) {
        return BlockLootRegistry.getOrGenerate(block);
    }

    static @NotNull Collection<Loot.@NotNull Generator> values() {
        return BlockLootRegistry.getAll();
    }
}
