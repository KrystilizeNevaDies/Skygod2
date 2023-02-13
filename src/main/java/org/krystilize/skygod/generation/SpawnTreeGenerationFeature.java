package org.krystilize.skygod.generation;

import de.articdive.jnoise.JNoise;
import net.minestom.server.instance.block.Block;
import org.krystilize.skygod.block.Blocks;

class SpawnTreeGenerationFeature implements GenerationFeature {

    @Override
    public void apply(Block.Setter setter) {
        final JNoise trunkNoise = JNoise.newBuilder()
                .fastSimplex()
                .setSeed(Integer.parseInt(System.getProperty("skygod_generation_seed", "0")))
                .setFrequency(0.1)
                .build();

        final JNoise leavesNosie = JNoise.newBuilder()
                .fastSimplex()
                .setSeed(Integer.parseInt(System.getProperty("skygod_generation_seed", "0")) + 1)
                .setFrequency(0.15)
                .build();

        int trunkHeight = 20;
        int trunkWidth = 2;
        int leavesWidth = 10;

        // Tree leaves
        for (int x = -leavesWidth; x < leavesWidth; x++) {
            for (int y = -leavesWidth; y < leavesWidth; y++) {
                for (int z = -leavesWidth; z < leavesWidth; z++) {
                    double distance = Math.sqrt(x * x + y * y + z * z);
                    if (distance > leavesWidth) continue;
                    double delta = distance / leavesWidth;
                    double noiseValue = Math.abs(leavesNosie.getNoise(x, y, z));
                    boolean hasBlock = delta + noiseValue < 1;
                    if (hasBlock) {
                        setter.setBlock(x, trunkHeight + y, z, noiseValue < 0.5 ? Blocks.TREE_LEAVES : Blocks.TREE_TRUNK);
                    }
                }
            }
        }

        // Tree trunk
        for (int y = 0; y < trunkHeight; y++) {
            // Make the width larger when closer to 0
//            int width = trunkWidth * (1 - y / trunkHeight);
            double closeToGround = ((double) trunkHeight - (double) y) / (double) trunkHeight;
            closeToGround = Math.pow(closeToGround, 2);
            closeToGround *= 0.7;
            closeToGround = 1 - closeToGround;
            int width = (int) (trunkWidth / closeToGround);
            for (int x = -width; x < width; x++) {
                for (int z = -width; z < width; z++) {
                    double distance = Math.sqrt(x * x + z * z);
                    if (distance > width) continue;
                    double delta = distance / width;
                    double noiseValue = Math.abs(trunkNoise.getNoise(x, y, z));
                    boolean isTrunk = delta + noiseValue < 1.3;
                    if (isTrunk)
                        setter.setBlock(x, y, z, Blocks.TREE_TRUNK);
                }
            }
        }
    }
}
