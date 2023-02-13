package org.krystilize.skygod.loot;

import org.krystilize.skygod.item.Item;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

record RandomLootImpl(List<Entry> entries, double totalWeight) implements Loot {
    @Override
    public List<Item> generate() {
        Random random = ThreadLocalRandom.current();
        double value = random.nextDouble(totalWeight);
        for (Entry entry : entries) {
            value -= entry.weight;
            if (value < 0) {
                return entry.loot().generate();
            }
        }
        throw new IllegalStateException("No loot found");
    }

    @Override
    public Set<List<Item>> generatePossible() {
        return entries.stream()
                .map(Entry::loot)
                .map(Loot::generatePossible)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    record Entry(Loot loot, double weight) {
    }

    static class Builder implements RandomBuilder {
        public Builder() {
        }

        private final List<Entry> entries = new ArrayList<>();
        private double totalWeight = 0;

        @Override
        public RandomBuilder entry(Loot loot, double weight) {
            entries.add(new Entry(loot, weight));
            totalWeight += weight;
            return this;
        }

        @Override
        public Loot build() {
            return new RandomLootImpl(entries, totalWeight);
        }
    }
}
