package org.krystilize.skygod.generation;

import de.articdive.jnoise.JNoise;
import net.minestom.server.instance.block.Block;
import org.krystilize.skygod.block.Blocks;

class SpawnIslandGenerationFeature implements GenerationFeature {
    @Override
    public void apply(Block.Setter setter) {
        final JNoise ground = JNoise.newBuilder()
                .fastSimplex()
                .setSeed(Integer.parseInt(System.getProperty("skygod_generation_seed", "0")))
                .setFrequency(0.1)
                .build();

        final JNoise terrain = JNoise.newBuilder()
                .fastSimplex()
                .setSeed(Integer.parseInt(System.getProperty("skygod_generation_seed", "0")) + 1)
                .setFrequency(0.025)
                .build();
        double height = 15;
        double width = 15;
        double maxTerrainHeight = 3;

        // Ground first
        {
            // Start with the top ground layer at y=0;
            for (double y = 0; y > -height; y--) {
                // layerDelta is closer to 1 the higher up we are
                double layerDelta = (height + y) / height;
                layerDelta = Math.pow(layerDelta, 0.5);
                double layerWidth = layerDelta * width;

                for (int x = (int) -layerWidth; x < layerWidth; x++) {
                    for (int z = (int) -layerWidth; z < layerWidth; z++) {
                        double distance = Math.sqrt(x * x + z * z);
                        if (distance > layerWidth) continue;
                        double noise;
                        synchronized (ground) {
                            noise = Math.abs(ground.getNoise(x, y, z));
                        }
                        boolean isDirt = layerDelta + noise > 1;
                        setter.setBlock(x, (int) y, z, isDirt ? Blocks.DIRT : Blocks.STONE);
                    }
                }
            }
        }

        // Now Terrain
        {
            for (int x = (int) -width; x < width; x++) {
                for (int z = (int) -width; z < width; z++) {
                    double distance = Math.sqrt(x * x + z * z);
                    if (distance > width) continue;
                    double noise;
                    synchronized (terrain) {
                        noise = Math.abs(terrain.getNoise(x, z));
                    }
                    double terrainHeight = noise * maxTerrainHeight;

                    for (int i = 1; i < terrainHeight; i++) {
                        setter.setBlock(x, i, z, Block.DIRT);
                    }
                    setter.setBlock(x, (int) terrainHeight, z, Blocks.GRASS_BLOCK);
                }
            }
        }

        // Now place the tree
        GenerationFeature.SPAWN_TREE.apply(setter);
    }
}
