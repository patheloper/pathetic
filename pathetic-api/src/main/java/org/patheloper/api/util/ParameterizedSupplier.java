package org.patheloper.api.util;

@FunctionalInterface
/** Represents a supplier that accepts a parameter */
public interface ParameterizedSupplier<T> {

  T accept(T value);
}
