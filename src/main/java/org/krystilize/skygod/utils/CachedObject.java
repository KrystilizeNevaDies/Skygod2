package org.krystilize.skygod.utils;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class CachedObject<T> {
    public abstract T get();

    public static <T> CachedObject<T> from(T value) {
        return from(() -> value);
    }

    public static <T> CachedObject<T> from(Supplier<T> supplier) {
        return new SupplierBacked<>(supplier);
    }

    private static class SupplierBacked<T> extends CachedObject<T> {

        private final Supplier<T> supplier;
        private T value;

        public SupplierBacked(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() {
            if (value == null) {
                value = supplier.get();
            }
            return value;
        }

        @Override
        public String toString() {
            return Objects.toString(get());
        }
    }
}
