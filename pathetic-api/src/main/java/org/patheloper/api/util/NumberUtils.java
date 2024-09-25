package org.patheloper.api.util;

import lombok.experimental.UtilityClass;

/**
 * A utility class that provides common mathematical operations such as interpolation, squaring, and
 * square root calculation. Designed to be used statically.
 */
@UtilityClass
public final class NumberUtils {

  /**
   * Linearly interpolates between two values, {@code a} and {@code b}, based on the provided
   * progress factor.
   *
   * <p>Formula: {@code a + (b - a) * progress}
   *
   * @param a the start value
   * @param b the end value
   * @param progress the interpolation factor (usually between 0 and 1)
   * @return the interpolated value
   */
  public static double interpolate(double a, double b, double progress) {
    return a + (b - a) * progress;
  }

  /**
   * Squares the provided value.
   *
   * @param value the value to square
   * @return the squared value
   */
  public static double square(double value) {
    return value * value;
  }

  /**
   * Approximates the square root of the input value.
   *
   * <p>This method provides a fast approximation of the square root using a combination of bit
   * manipulation and Newton's method for refinement.
   *
   * @param input the value to compute the square root of
   * @return the approximated square root
   */
  public static double sqrt(double input) {
    // initial approximation using bit manipulation
    double sqrt =
        Double.longBitsToDouble((Double.doubleToLongBits(input) - (1L << 52) >> 1) + (1L << 61));

    // refine the approximation using newtons method
    double better = (sqrt + input / sqrt) / 2.0;

    return (better + input / better) / 2.0;
  }
}
