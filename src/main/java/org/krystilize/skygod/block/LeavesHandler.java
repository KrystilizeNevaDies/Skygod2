package org.krystilize.skygod.block;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;
import org.krystilize.skygod.loot.BlockLoot;
import org.krystilize.skygod.loot.Loot;
import org.krystilize.skygod.utils.RandomTicker;

record LeavesHandler() implements BlockHandler, RandomTicker.Handler {

    @Override
    public @NotNull NamespaceID getNamespaceId() {
        return NamespaceID.from("skygod:leaves_handler");
    }

    @Override
    public void randomTick(RandomTick randomTick) {
        Instance instance = randomTick.instance();
        Block block = randomTick.block();
        Point pos = randomTick.point();

        boolean surroundedByAir = instance.getBlock(pos.add(1, 0, 0)).isAir() &&
                instance.getBlock(pos.add(-1, 0, 0)).isAir() &&
                instance.getBlock(pos.add(0, 1, 0)).isAir() &&
                instance.getBlock(pos.add(0, -1, 0)).isAir() &&
                instance.getBlock(pos.add(0, 0, 1)).isAir() &&
                instance.getBlock(pos.add(0, 0, -1)).isAir();

        if (surroundedByAir) {
            instance.setBlock(pos, Block.AIR);
            var loot = BlockLoot.from(block).generate(new Loot.Context() {});
            loot.spawnAt(instance, pos);
        }
    }
}
