package org.krystilize.skygod;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.krystilize.skygod.crafting.CraftingGUI;
import org.krystilize.skygod.events.ItemGrindEvent;
import org.krystilize.skygod.generation.GenerationFeature;
import org.krystilize.skygod.item.Item;
import org.krystilize.skygod.loot.BlockLoot;
import org.krystilize.skygod.loot.Loot;
import org.krystilize.skygod.utils.BlockUtils;
import org.krystilize.skygod.utils.DimensionTypeUtils;
import org.krystilize.skygod.utils.RandomTicker;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class SkygodPlayerInstance extends InstanceContainer {

    /**
     * Creates the skygod player instance for the given uuid.
     *
     * @param playerUUID the uuid of the player
     * @return the skygod player instance
     */
    public static @NotNull SkygodPlayerInstance create(@NotNull UUID playerUUID) {
        return new SkygodPlayerInstance(playerUUID);
    }

    private SkygodPlayerInstance(@NotNull UUID playerUUID) {
        super(playerUUID, DimensionTypeUtils.FULLBRIGHT, generatePlayerChunkLoader(playerUUID));
        registerEvents(eventNode());
        setGenerator(unit -> {
            Point min = unit.absoluteStart();
            Point max = unit.absoluteEnd();

            // If this unit does not contain 0, 0, 0, exit early
            if ((min.x() > 0 || min.y() > 0 || min.z() > 0) ||
                    (max.x() <= 0 || max.y() <= 0 || max.z() <= 0)) {
                return;
            }

            // generate the spawn island
            unit.fork(GenerationFeature.SPAWN_ISLAND.offset(0, 39, 0));
        });
    }

    private static IChunkLoader generatePlayerChunkLoader(UUID playerUUID) {
        // TODO: Implement chunk loading
        return null;
    }

    // TODO: Should events be registered here?
    private record PlayerBlockBreakContext(Player player, Block block) implements Loot.Context, Loot.Context.HasPlayer,
            Loot.Context.HasBlock {
    }

    private record PlayerToolBlookBreakContext(Player player, Block block, Item tool) implements Loot.Context,
            Loot.Context.HasPlayer, Loot.Context.HasBlock, Loot.Context.HasTool {
    }

    private static void registerEvents(EventNode<InstanceEvent> node) {
        node.addListener(PlayerBlockBreakEvent.class, event -> {
            if (event.isCancelled()) {
                return;
            }

            Player player = event.getPlayer();
            Block block = event.getBlock();
            Item tool = Item.fromItemStack(player.getItemInMainHand());

            Loot.Context context = tool == null ? new PlayerBlockBreakContext(player, block) :
                    new PlayerToolBlookBreakContext(player, block, tool);

            Loot loot = BlockLoot.from(block).generate(context);
            loot.spawnAt(player.getInstance(), event.getBlockPosition());
        });
        node.addListener(PickupItemEvent.class, event -> {
            if (event.isCancelled()) return;
            Entity entity = event.getEntity();
            if (!(entity instanceof Player player)) return;
            player.getInventory().addItemStack(event.getItemStack());
        });
        node.addListener(PlayerBlockPlaceEvent.class, event -> {
            Player player = event.getPlayer();
            Player.Hand hand = event.getHand();
            ItemStack item = player.getItemInHand(hand);

            Block blockToPlace = item.getTag(BlockUtils.BLOCK_TO_PLACE);
            if (blockToPlace == null) {
                event.setCancelled(true);
            } else {
                event.setBlock(blockToPlace);
            }
        });

        node.addListener(InstanceTickEvent.class, event ->
                RandomTicker.tick(event.getInstance(), ThreadLocalRandom.current()));

        node.addListener(InventoryPreClickEvent.class, event -> {
            int slot = event.getSlot();
            if (slot == 36) {
                event.setCancelled(true);
                CraftingGUI gui = new CraftingGUI(event.getPlayer().getUuid());
                gui.open(event.getPlayer());
            }
        });
    }
}
