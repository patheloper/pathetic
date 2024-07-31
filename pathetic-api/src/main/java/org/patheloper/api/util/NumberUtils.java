package org.patheloper.api.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class NumberUtils {

  public static double interpolate(double a, double b, double progress) {
    return a + (b - a) * progress;
  }

  public static double square(double value) {
    return value * value;
  }

  public static double sqrt(double input) {

    double sqrt =
        Double.longBitsToDouble((Double.doubleToLongBits(input) - (1L << 52) >> 1) + (1L << 61));
    double better = (sqrt + input / sqrt) / 2.0;

    return (better + input / better) / 2.0;
  }
}
