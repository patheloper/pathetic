package org.patheloper.api.util;

@FunctionalInterface
public interface ParameterizedSupplier<T> {

    T accept(T value);
}
