package org.krystilize.skygod.loot;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.krystilize.skygod.item.Item;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public interface Loot {

    static RandomBuilder random() {
        return new RandomLootImpl.Builder();
    }

    static Loot none() {
        return List::of;
    }

    static Loot oneOf(Loot... loots) {
        RandomBuilder builder = random();
        for (Loot loot : loots) {
            builder.entry(loot, 1);
        }
        return builder.build();
    }

    static Loot allOf(Loot... loots) {
        return () -> Arrays.stream(loots)
                .map(Loot::generate)
                .flatMap(Collection::stream)
                .toList();
    }

    static Loot count(Item item, int count) {
        Item[] items = new Item[count];
        Arrays.fill(items, item);
        List<Item> list = List.of(items);
        return () -> list;
    }

    static Loot randomCount(Item item, int minInclusive, int maxExclusive) {
        RandomBuilder builder = random();
        for (int i = minInclusive; i < maxExclusive; i++) {
            builder.entry(Loot.count(item, i), 1);
        }
        return builder.build();
    }

    static Loot single(Item item) {
        return count(item, 1);
    }

    interface RandomBuilder {
        RandomBuilder entry(Loot loot, double weight);

        Loot build();
    }

    List<Item> generate();
    default Set<List<Item>> generatePossible() {
        return Set.of(generate());
    }

    default CompletableFuture<Void> spawnAt(Instance instance, Point pos) {
        Point spawnPos = pos.add(0.5);
        //noinspection rawtypes
        CompletableFuture[] futures = generate()
                .stream()
                .map(Item::stack)
                .map(ItemEntity::new)
                .peek(entity -> {
                    ThreadLocalRandom random = ThreadLocalRandom.current();
                    double randX = random.nextDouble(-1, 1);
                    double randY = random.nextDouble(-1, 1);
                    double randZ = random.nextDouble(-1, 1);
                    entity.setVelocity(new Vec(randX, randY, randZ).normalize().mul(random.nextDouble(0, 2)));
                })
                .map(entity -> entity.setInstance(instance, spawnPos))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures);
    }

    interface Context {
        interface HasInstance extends Context {
            Instance instance();
        }

        interface HasBlock extends Context {
            Block block();
        }

        interface HasEntity extends Context, HasInstance {
            Entity entity();

            default Instance instance() {
                return entity().getInstance();
            }
        }

        interface HasPlayer extends Context, HasEntity {
            Player player();

            default Entity entity() {
                return player();
            }
        }

        interface HasTool extends Context, HasBlock {
            Item tool();
        }
    }

    interface Generator {
        static Generator of(Loot... loots) {
            return new Generator() {
                @Override
                public @NotNull Loot generate(Context context) {
                    return Loot.oneOf(loots);
                }

                @Override
                public @NotNull Set<Loot> generatePossible() {
                    return Set.of(loots);
                }
            };
        }

        @NotNull Loot generate(Context context);

        @NotNull Set<Loot> generatePossible();
    }
}
