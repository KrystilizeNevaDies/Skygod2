package org.krystilize.skygod.block;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.krystilize.skygod.utils.RandomTicker;

record GrassHandler() implements BlockHandler, RandomTicker.Handler {

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("skygod:grass_handler");
    }

    @Override
    public void randomTick(RandomTick randomTick) {
        Instance instance = randomTick.instance();
        Block block = randomTick.block();
        Point pos = randomTick.point();

        if (instance.getBlock(pos.add(0, 1, 0)).isSolid()) {
            instance.setBlock(pos, Block.DIRT);
            return;
        }

        tryInfect(instance, block, pos.add(-1, 0, 0));
        tryInfect(instance, block, pos.add(1, 0, 0));
        tryInfect(instance, block, pos.add(0, 0, -1));
        tryInfect(instance, block, pos.add(0, 0, 1));

        tryInfect(instance, block, pos.add(-1, 1, 0));
        tryInfect(instance, block, pos.add(1, 1, 0));
        tryInfect(instance, block, pos.add(0, 1, -1));
        tryInfect(instance, block, pos.add(0, 1, 1));

        tryInfect(instance, block, pos.add(-1, -1, 0));
        tryInfect(instance, block, pos.add(1, -1, 0));
        tryInfect(instance, block, pos.add(0, -1, -1));
        tryInfect(instance, block, pos.add(0, -1, 1));
    }

    private void tryInfect(Instance instance, Block blockToSpread, Point pos) {
        Block blockAbove = instance.getBlock(pos.add(0, 1, 0), Block.Getter.Condition.NONE);
        if (blockAbove.isSolid()) return;

        Block block = instance.getBlock(pos, Block.Getter.Condition.NONE);
        if (block.compare(Block.DIRT)) {
            instance.setBlock(pos, blockToSpread);
        }
    }
}
