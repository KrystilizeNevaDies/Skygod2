package org.krystilize.skygod;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import org.krystilize.skygod.block.Blocks;
import org.krystilize.skygod.crafting.CraftingGUI;
import org.krystilize.skygod.crafting.CraftingRecipe;
import org.krystilize.skygod.debug.DebugFeatures;
import org.krystilize.skygod.generation.GenerationFeature;
import org.krystilize.skygod.item.Item;
import org.krystilize.skygod.loot.BlockLoot;
import org.krystilize.skygod.utils.ComponentUtils;
import org.krystilize.skygod.utils.SkygodHubInstance;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BootstrapSkygod {
    public static final Logger LOGGER = Logger.getLogger("Skygod");

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void main(String[] args) {
        // TODO: Abstract all of this logic out of the bootstrap

        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        Block.values();

        // Load registries
        {
            long start = System.currentTimeMillis();
            LOGGER.log(Level.INFO, "Loading registries...");
            Blocks.values();
            CraftingRecipe.values();
            Item.values();
            BlockLoot.values();
            LOGGER.log(Level.INFO, "Loaded registries in " + (System.currentTimeMillis() - start) + "ms");
        }

        // Create the hub instance
        InstanceContainer hub = SkygodHubInstance.create();
        instanceManager.registerInstance(hub);
        hub.setGenerator(unit ->
                unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            Instance instance = SkygodPlayerInstance.create(player.getUuid());
            instanceManager.registerInstance(instance);
            event.setSpawningInstance(instance);
            player.setRespawnPoint(new Pos(10, 42, 0));
            player.setGameMode(GameMode.SURVIVAL);
        });
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            if (!event.isFirstSpawn()) {
                return;
            }
            Player player = event.getPlayer();
            player.getInventory().setItemStack(36, CraftingGUI.DEFAULT_CRAFTING_BOOK);
        });

        // Initialize component utils for the click handlers
        ComponentUtils.init(MinecraftServer.process());

        // Enable debug features
        DebugFeatures.enable(MinecraftServer.process());

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);
    }
}