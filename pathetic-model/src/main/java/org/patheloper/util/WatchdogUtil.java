package org.patheloper.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;

@UtilityClass
public class WatchdogUtil {

  private Class<?> watchdogClass = null;
  private Method tickMethod = null;

  static {
    try {
      watchdogClass = Class.forName("org.spigotmc.WatchdogThread");
      tickMethod = watchdogClass.getDeclaredMethod("tick");
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      throw ErrorLogger.logFatalError(
          "Failed to initialize Watchdog class or tick method: " + e.getMessage(), e);
    }
  }

  /**
   * Invokes the tick method on the Spigot Watchdog thread, if available. This ensures the server
   * watchdog timer is properly reset.
   */
  public void tickWatchdog() {
    if (Bukkit.isPrimaryThread()) {
      invokeTickIfAvailable();
    }
  }

  /** Checks if the Watchdog class and tick method are available, and invokes the tick method. */
  private static void invokeTickIfAvailable() {
    if (tickMethod != null && watchdogClass != null) {
      invokeTickMethod();
    }
  }

  /** Safely invokes the tick method on the Watchdog class to reset the watchdog timer. */
  private static void invokeTickMethod() {
    try {
      tickMethod.invoke(watchdogClass);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw ErrorLogger.logFatalError(
          "Failed to invoke Watchdog tick method: " + e.getMessage(), e);
    }
  }
}
