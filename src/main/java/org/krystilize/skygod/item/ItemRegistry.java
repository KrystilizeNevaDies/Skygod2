package org.krystilize.skygod.item;

import com.moandjiezana.toml.Toml;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minestom.server.item.Material;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

class ItemRegistry {
    static final Int2ObjectMap<Item> STATE_IDS;
    static final Map<String, Item> REGISTRY;

    static {
        List<ItemImpl.Builder.FinisherStep> finishers = new ArrayList<>();

        // TODO: Color
        Toml toml = new Toml().read(new File("items.toml"));
        for (Map.Entry<String, Object> entry : toml.entrySet()) {
            String id = entry.getKey();
            Toml data = (Toml) entry.getValue();
            String displayName = Objects.requireNonNull(data.getString("displayName"), "displayName not found in items.");
            String material = Objects.requireNonNull(data.getString("material"), "material not found in items.");
            int matter = Objects.requireNonNull(data.getLong("matter")).intValue();
            List<String> properties = data.getList("properties", List.of());
            String block = data.getString("block", null);
            String grindingOutput = data.getString("grindingOutput", null);

            var finisher = ItemImpl.builder(id)
                    .displayName(displayName)
                    .material(Material.fromNamespaceId(material))
                    .matter(matter)
                    .properties(properties.stream().map(ItemProperty::from).toArray(ItemProperty[]::new))
                    .block(block)
                    .grindingOutput(grindingOutput);
            finishers.add(finisher);
        }

        finishers.sort(Comparator.comparing(ItemImpl.Builder.FinisherStep::id));

        // Compile the blocks into the registry
        Map<String, Item> finished = new HashMap<>();
        int stateId = 0;
        for (ItemImpl.Builder.FinisherStep finisher : finishers) {
            Item item = finisher.build(stateId);
            finished.put(item.id(), item);
            stateId += finisher.stateIdSize();
        }
        REGISTRY = Map.copyOf(finished);
        Map<Integer, Item> id2item = finished.values()
                .stream()
                .map(item -> Map.entry(item.stateId(), item))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        STATE_IDS = Int2ObjectMaps.unmodifiable(new Int2ObjectOpenHashMap<>(id2item));
    }
}
