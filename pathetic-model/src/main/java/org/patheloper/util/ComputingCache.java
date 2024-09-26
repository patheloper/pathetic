package org.patheloper.util;

import java.util.function.Supplier;

/**
 * A simple caching mechanism that computes a value on demand and caches it for future access
 *
 * @param <T> the type of the cached value
 */
public class ComputingCache<T> {

  /** A supplier that provides the value when first requested. */
  private final Supplier<T> supplier;

  /** The cached value, initialized lazily when requested. */
  private T value;

  /**
   * Constructs a new {@link ComputingCache} with the given supplier.
   *
   * @param supplier the supplier that computes the value when needed
   */
  public ComputingCache(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  /**
   * Returns the cached value. If the value has not yet been computed, the supplier is used to
   * compute it, and the result is stored for future access.
   *
   * @return the cached value
   */
  public T get() {
    if (this.value == null) {
      this.value = this.supplier.get();
    }
    return this.value;
  }
}
