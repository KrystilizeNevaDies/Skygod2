package org.krystilize.skygod.generation;

import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Represents a single structure in the world that can be generated.
 * This does not allow information from the world to be queried before generating.
 */
public interface GenerationFeature extends Consumer<Block.Setter> {
    /**
     * Applies this generation feature to the given block setter.
     * This should place the feature around x = 0, y = 0, z = 0.
     *
     * @param setter the block setter to use
     */
    void apply(Block.Setter setter);

    @Override
    @ApiStatus.Internal
    default void accept(Block.Setter setter) {
        apply(setter);
    }

    /**
     * Offsets this generation feature by the given amount.
     *
     * @param offsetX the amount to offset the x coordinate by
     * @param offsetY the amount to offset the y coordinate by
     * @param offsetZ the amount to offset the z coordinate by
     * @return the new generation feature
     */
    default @NotNull GenerationFeature offset(int offsetX, int offsetY, int offsetZ) {
        final GenerationFeature feature = this;
        return setter -> {
            feature.apply((x, y, z, block) -> {
                setter.setBlock(x + offsetX, y + offsetY, z + offsetZ, block);
            });
        };
    }

    GenerationFeature SPAWN_ISLAND = new SpawnIslandGenerationFeature();
    GenerationFeature SPAWN_TREE = new SpawnTreeGenerationFeature().offset(0, -2, 0);
}
