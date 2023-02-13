package org.krystilize.skygod.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.Nullable;
import org.krystilize.skygod.block.Blocks;
import org.krystilize.skygod.utils.BlockUtils;
import org.krystilize.skygod.utils.CachedObject;
import org.krystilize.skygod.utils.ComponentUtils;

import java.util.*;
import java.util.stream.Collectors;

record ItemImpl(ItemData data, Map<ItemProperty<?>, Object> properties, int stateIdOrigin, int stateId) implements Item {

    public ItemImpl(ItemData data, Map<ItemProperty<?>, Object> properties, int stateIdOrigin) {
        this(data, properties, stateIdOrigin, calculateStateId(properties, stateIdOrigin));
    }

    public ItemImpl {
        properties = Map.copyOf(properties);
    }

    @Override
    public String toString() {
        return id() + "(" +
                this.properties().entrySet().stream()
                        .map(entry -> entry.getKey().id() + "=" + entry.getValue())
                        .collect(Collectors.joining(", ")) +
                ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ItemImpl other = (ItemImpl) obj;
        return stateId() == other.stateId();
    }

    public static Builder.DisplayNameStep builder(String id) {
        var builder = new BuilderImpl();
        builder.id = id;
        return builder;
    }

    @Override
    public ItemStack stack() {
        return ItemStack.builder(data().material())
                .set(Item.TAG_ID, data().id())
                .set(BlockUtils.BLOCK_TO_PLACE, data().blockToPlace())
                .displayName(data().displayName())
                .meta(builder -> builder.customModelData(stateId()))
                .lore(properties().entrySet().stream()
                        .map(entry -> entry.getKey().id() + " -> " + entry.getValue())
                        .map(ComponentUtils::text)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public String id() {
        return data().id();
    }

    @Override
    public @Nullable Item grindingOutput() {
        return data().grindingOutput();
    }

    @Override
    public TextColor color() {
        return data().color();
    }

    @Override
    public int matter() {
        return data().matter();
    }

    private static int calculateStateId(Map<ItemProperty<?>, Object> properties, int stateIdOrigin) {
        if (properties.size() == 0) {
            return stateIdOrigin;
        }
        int stateId = stateIdOrigin;
        int totalStates = properties
                .keySet()
                .stream()
                .mapToInt(property -> property.values().size())
                .reduce(1, (a, b) -> a * b);
        for (Map.Entry<ItemProperty<?>, Object> entry : properties.entrySet()
                .stream().sorted(Comparator.comparing(entry -> entry.getKey())).toList()) {
            ItemProperty<?> property = entry.getKey();
            Object value = entry.getValue();
            List<?> values = property.values();

            int index = values.indexOf(value);
            stateId += index * (totalStates / values.size());
        }
        return stateId;
    }

    @Override
    public <T> Item withProperty(ItemProperty<T> property, T value) {
        if (!properties.containsKey(property)) {
            throw new IllegalArgumentException("Property " + property.id() + " is not defined for item " + data().id());
        }
        Map<ItemProperty<?>, Object> newProperties = new HashMap<>(properties);
        newProperties.put(property, value);
        return new ItemImpl(data, newProperties, stateIdOrigin);
    }

    interface Builder {
        interface DisplayNameStep {
            MaterialStep displayName(String displayName);
        }

        interface MaterialStep {
            MatterStep material(Material material);
        }

        interface MatterStep {
            PropertiesStep matter(int matter);
        }

        interface PropertiesStep {
            BlockStep properties(ItemProperty<?>... properties);
        }

        interface BlockStep {
            GrindingOutputStep block(@Nullable String block);
        }


        interface GrindingOutputStep {
            FinisherStep grindingOutput(@Nullable String grindingOutput);
        }

        interface FinisherStep {
            int stateIdSize();

            Item build(int stateIdOrigin);

            String id();
        }
    }

    private static class BuilderImpl implements Builder.DisplayNameStep, Builder.MaterialStep, Builder.MatterStep,
            Builder.PropertiesStep, Builder.BlockStep, Builder.GrindingOutputStep, Builder.FinisherStep {

        private String id;
        private String displayName;
        private Material material;
        private int matter;
        private ItemProperty<?>[] properties;
        private @Nullable String block;
        private String grindingOutput;

        @Override
        public Builder.MaterialStep displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        @Override
        public Builder.MatterStep material(Material material) {
            this.material = material;
            return this;
        }

        @Override
        public Builder.PropertiesStep matter(int matter) {
            this.matter = matter;
            return this;
        }

        @Override
        public Builder.BlockStep properties(ItemProperty<?>... properties) {
            this.properties = properties;
            return this;
        }

        @Override
        public Builder.GrindingOutputStep block(@Nullable String block) {
            this.block = block;
            return this;
        }

        @Override
        public Builder.FinisherStep grindingOutput(@Nullable String grindingOutput) {
            this.grindingOutput = grindingOutput;
            return this;
        }

        @Override
        public int stateIdSize() {
            return Math.max(Arrays.stream(properties)
                    .mapToInt(property -> property.values().size())
                    .reduce(1, (a, b) -> a * b), 1);
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public Item build(int stateIdOrigin) {
            Map<ItemProperty<?>, Object> propertyMap = Arrays.stream(properties)
                    .map(property -> Map.entry(property, property.defaultValue()))
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
            // TODO: TextColor in builder
            TextColor color = TextColor.color(Objects.hashCode(id));
            CachedObject<Block> blockCache = CachedObject.from(() -> block == null ? null : Blocks.from(block));
            CachedObject<Item> grindingOutputCache = CachedObject.from(() ->
                    grindingOutput == null ? null : Item.from(grindingOutput));
            ItemData itemData = new ItemData(id, material, matter, blockCache, ComponentUtils.text(displayName), stateIdOrigin,
                    grindingOutputCache, color);
            return new ItemImpl(itemData, propertyMap, stateIdOrigin);
        }
    }
}
