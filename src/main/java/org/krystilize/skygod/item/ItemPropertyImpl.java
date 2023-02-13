package org.krystilize.skygod.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

record ItemPropertyImpl<T>(String id, T defaultValue, Function<T, String> valueToString,
                           Function<String, T> stringToValue, List<T> values) implements ItemProperty<T> {

    static final Map<String, ItemProperty<?>> REGISTRY;

    @SafeVarargs
    private static <T> ItemPropertyImpl<T> from(String id, Function<T, String> valueToString,
                                                Function<String, T> stringToValue, T... values) {
        if (values.length == 0) {
            throw new IllegalArgumentException("At least one value is required");
        }
        return new ItemPropertyImpl<>(id, values[0], valueToString, stringToValue, List.of(values));
    }

    public static ItemPropertyImpl<Boolean> bool(String key, Boolean... values) {
        return from(key, Object::toString, Boolean::parseBoolean, values);
    }

    /**
     * @param min inclusive
     * @param max exclusive
     */
    private static ItemPropertyImpl<Integer> integer(String key, int defaultValue, int min, int max) {
        List<Integer> values = new ArrayList<>();
        values.add(defaultValue);
        for (int i = min; i < max; i++) {
            values.add(i);
        }
        return from(key, Object::toString, Integer::parseInt, values.toArray(Integer[]::new));
    }

    private static <E extends Enum<E>> ItemProperty<E> mapEnum(String key, Class<E> enumClass) {
        E[] values = enumClass.getEnumConstants();
        return from(key, Enum::name, string -> Enum.valueOf(enumClass, string), values);
    }

    static {
        // TODO: config registry
        Map<String, ItemProperty<?>> map = new HashMap<>();
        map.put("broken", ItemPropertyImpl.bool("broken", false, true));
        map.put("shiny", ItemPropertyImpl.bool("shiny", false, true));
        map.put("liquid_level", ItemPropertyImpl.mapEnum("liquid_level", ItemProperty.LiquidLevel.class));

        REGISTRY = Map.copyOf(map);
    }
}
