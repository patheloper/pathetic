package org.patheloper;

import lombok.experimental.UtilityClass;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A utility class that handles the setup and management of bStats metrics for tracking library
 * usage
 */
@UtilityClass
public class BStatsHandler {

  private int paths = 0;

  /**
   * Initializes the bStats metrics for tracking plugin usage statistics.
   *
   * @param javaPlugin The JavaPlugin instance used to set up metrics.
   */
  public void init(JavaPlugin javaPlugin) {
    Metrics metrics = new Metrics(javaPlugin, 20529);
    metrics.addCustomChart(new SingleLineChart("total_paths", BStatsHandler::resetAndReturnPaths));
    metrics.addCustomChart(new SimplePie("pathetic-model_version", Pathetic::getModelVersion));
  }

  /** Increases the path count in a thread-safe manner. */
  public synchronized void increasePathCount() {
    paths++;
  }

  /**
   * Resets the path count to zero and returns the previous count.
   *
   * @return The total number of paths recorded before resetting.
   */
  private synchronized int resetAndReturnPaths() {
    int totalPaths = paths;
    paths = 0; // Reset the path count after reporting
    return totalPaths;
  }
}
