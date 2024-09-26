package org.patheloper.util;

import lombok.Value;

/**
 * A generic immutable tuple class representing a triplet of values.
 *
 * @param <T> the type of the elements in this tuple
 */
@Value
public class Tuple3<T> {

  T x;
  T y;
  T z;

  /**
   * Constructs a Tuple3 with three values.
   *
   * @param x the first value
   * @param y the second value
   * @param z the third value
   */
  public Tuple3(T x, T y, T z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
