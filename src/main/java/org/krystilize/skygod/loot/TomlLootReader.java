package org.krystilize.skygod.loot;

import com.moandjiezana.toml.Toml;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.krystilize.skygod.block.Blocks;
import org.krystilize.skygod.item.Item;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

class TomlLootReader {

    public static Map<String, Loot.Generator> read(Toml toml) {
        Map<String, Loot.Generator> map = new HashMap<>();
        TomlLootReader reader = new TomlLootReader();
        for (Map.Entry<String, Object> entry : toml.entrySet()) {
            String id = entry.getKey();
            Toml data = (Toml) entry.getValue();
            map.put(id, reader.readUnknown(id, data));
        }
        return Map.copyOf(map);
    }

    private final Map<String, BiFunction<String, Toml, Loot.Generator>> readers = Map.of(
            "none", this::readNone,
            "item", this::readItem,
            "random", this::readRandom
    );

    private Loot.Generator readUnknown(String id, Toml toml) {
        String type = Objects.requireNonNull(toml.getString("type"), "type not found in loot.");
        var reader = readers.get(type.toLowerCase(Locale.ROOT));
        Objects.requireNonNull(reader, "No reader found for loot type: " + type);
        return reader.apply(id, toml);
    }

    private Loot.Generator readItem(String id, Toml toml) {
        Block block = Blocks.from(id);
        Objects.requireNonNull(block, "No block found for loot id: " + id);

        String itemID = toml.getString("item");
        Objects.requireNonNull(itemID, "item not found in loot.");

        Item item = Item.from(itemID);
        Objects.requireNonNull(item, "No item found for loot item id: " + itemID);

        int blockMatter = block.getTag(Blocks.MATTER_TAG);
        int itemMatter = item.matter();
        int itemCount = (int) Math.ceil(blockMatter / (double) itemMatter);

        return new Loot.Generator() {
            @Override
            public @NotNull Loot generate(Loot.Context context) {
                return Loot.count(item, itemCount);
            }

            @Override
            public @NotNull Set<Loot> generatePossible() {
                return Set.of(Loot.count(item, itemCount));
            }
        };
    }

    private Loot.Generator readNone(String id, Toml toml) {
        return Loot.Generator.of(Loot.none());
    }

    private Loot.Generator readRandom(String id, Toml toml) {
        List<Toml> entries = Objects.requireNonNull(toml.getTables("entries"), "entries not found in loot.");
        List<Entry> generator = entries.stream()
                .map(t -> readEntry(id, t))
                .toList();
        return new Loot.Generator() {
            @Override
            public @NotNull Loot generate(Loot.Context context) {
                Loot.RandomBuilder builder = Loot.random();
                for (Entry entry : generator) {
                    builder.entry(entry.loot().generate(context), entry.weight());
                }
                return builder.build();
            }

            @Override
            public @NotNull Set<Loot> generatePossible() {
                return generator.stream()
                        .map(Entry::loot)
                        .map(Loot.Generator::generatePossible)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());
            }
        };
    }

    private record Entry(Loot.Generator loot, int weight) {
    }

    private Entry readEntry(String id, Toml toml) {
        Toml lootTable = Objects.requireNonNull(toml.getTable("loot"), "loot table not found in random entry.");
        Loot.Generator loot = readUnknown(id, lootTable);
        int weight = Objects.requireNonNull(toml.getLong("weight"), "weight not found in random entry.").intValue();
        return new Entry(loot, weight);
    }
}
