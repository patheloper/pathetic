package org.patheloper;

import lombok.experimental.UtilityClass;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class BStatsHandler {

  private int paths;

  public void init(JavaPlugin javaPlugin) {
    Metrics metrics = new Metrics(javaPlugin, 20529);
    metrics.addCustomChart(
        new SingleLineChart(
            "total_paths",
            () -> {
              int totalPaths = paths;
              paths = 0;
              return totalPaths;
            }));
    metrics.addCustomChart(new SimplePie("pathetic-model_version", Pathetic::getModelVersion));
  }

  public synchronized void increasePathCount() {
    paths++;
  }
}
