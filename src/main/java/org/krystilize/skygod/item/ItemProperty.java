package org.krystilize.skygod.item;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemProperty<T> extends Comparable<ItemProperty<T>> {

    String id();

    T defaultValue();

    List<T> values();

    ItemProperty<Boolean> BROKEN = ItemProperty.from("broken");
    ItemProperty<Boolean> SHINY = ItemProperty.from("shiny");
    ItemProperty<LiquidLevel> LIQUID_LEVEL = ItemProperty.from("liquid_level");

    enum LiquidLevel {
        EMPTY,
        PERCENT_25,
        PERCENT_50,
        PERCENT_75,
        FULL
    }

    static <T> ItemProperty<T> from(String id) {
        //noinspection unchecked
        return (ItemProperty<T>) ItemPropertyImpl.REGISTRY.get(id);
    }

    @Override
    default int compareTo(@NotNull ItemProperty<T> o) {
        return id().compareTo(o.id());
    }
}
