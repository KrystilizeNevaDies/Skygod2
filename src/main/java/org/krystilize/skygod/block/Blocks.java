package org.krystilize.skygod.block;

import net.minestom.server.instance.block.Block;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;

// TODO: Turn this into a registry system
public non-sealed interface Blocks extends BlockConstants {

    /**
     * Matter is a measure of how much mass a block has.
     */
    Tag<Integer> MATTER_TAG = Tag.Integer("skygod:matter")
            .defaultValue(() -> {throw new IllegalStateException("Matter tag not set");});

    static @UnknownNullability Block from(@NotNull String id) {
        return BlocksRegistry.REGISTRY.get(id);
    }

    static @NotNull Collection<Block> values() {
        return BlocksRegistry.REGISTRY.values();
    }
}
