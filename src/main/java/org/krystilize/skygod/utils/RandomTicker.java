package org.krystilize.skygod.utils;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.Random;

public class RandomTicker {

    public static final int RANDOM_TICK_SPEED = Integer.parseInt(System.getProperty("skygod.random_tick_speed", "3"));

    public static void tick(Instance instance, Random random) {
        CHUNK_LOOP:
        for (Chunk chunk : instance.getChunks()) {
            // Ensure that the chunks have a 3x3 area around them of loaded chunks
            int chunkX = chunk.getChunkX();
            int chunkZ = chunk.getChunkZ();

            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    if (instance.getChunk(chunkX + x, chunkZ + z) == null) continue CHUNK_LOOP;
                }
            }

            int minSection = chunk.getMinSection();
            int maxSection = chunk.getMaxSection();


            int minX = chunk.getChunkX() * Chunk.CHUNK_SIZE_X;
            int minZ = chunk.getChunkZ() * Chunk.CHUNK_SIZE_Z;

            for (int section = minSection; section < maxSection; section++) {
                int minY = section * Chunk.CHUNK_SECTION_SIZE;

                for (int i = 0; i < RANDOM_TICK_SPEED; i++) {
                    int x = random.nextInt(minX, minX + Chunk.CHUNK_SIZE_X);
                    int y = random.nextInt(minY, minY + Chunk.CHUNK_SECTION_SIZE);
                    int z = random.nextInt(minZ, minZ + Chunk.CHUNK_SIZE_Z);
                    Vec pos = new Vec(x, y, z);
                    Block block = instance.getBlock(pos);
                    if (block.handler() instanceof Handler handler) {
                        handler.randomTick(new Handler.RandomTick(instance, block, pos));
                    }
                }
            }
        }
    }

    public interface Handler {
        record RandomTick(Instance instance, Block block, Point point) {
        }

        default void randomTick(RandomTick randomTick) {
        }
    }
}
