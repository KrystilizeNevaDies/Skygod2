package org.krystilize.skygod.debug;

import net.minestom.server.ServerProcess;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.krystilize.skygod.SkygodPlayerInstance;
import org.krystilize.skygod.item.Item;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import static net.minestom.server.command.builder.arguments.ArgumentType.Literal;

public record DebugFeatures(ServerProcess process) {
    public static void enable(ServerProcess process) {
        new DebugFeatures(process).enableLocal();
    }

    public void enableLocal() {
        {
            CommandManager manager = process.command();

            manager.register(new DebugCommand());
        }
    }


    private class DebugCommand extends Command {
        public DebugCommand() {
            super("debug");

            addSyntax(this::usageNewInstance, Literal("instance"));
            addSyntax(this::usageGiveItem, Literal("item"));
        }

        private void usageGiveItem(@NotNull CommandSender sender, @NotNull CommandContext context) {
            Collection<Item> items = Item.values();
            Item[] itemArray = items.toArray(Item[]::new);
            Random random = ThreadLocalRandom.current();

            for (int i = 0; i < 10; i++) {
                Item item = itemArray[random.nextInt(itemArray.length)];
                Player player = (Player) sender;
                player.getInventory().addItemStack(item.stack());
            }
        }

        private final AtomicInteger nextSeed = new AtomicInteger(0);

        private void usageNewInstance(@NotNull CommandSender sender, @NotNull CommandContext context) {
            Player player = (Player) sender;

            System.setProperty("skygod_generation_seed", Objects.toString(nextSeed.incrementAndGet()));

            Instance instance = SkygodPlayerInstance.create(player.getUuid());
            process().instance().registerInstance(instance);
            player.setInstance(instance).thenRun(() -> player.setGameMode(GameMode.SPECTATOR));
        }
    }
}
