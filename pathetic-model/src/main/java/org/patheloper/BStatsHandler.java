package org.patheloper;

import lombok.experimental.UtilityClass;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class BStatsHandler {

  private Metrics metrics;

  private int paths;

  private void init(JavaPlugin javaPlugin) {
    if (metrics != null) return;

    metrics = new Metrics(javaPlugin, 20529);
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

  private void makeSureBStatsIsInitialized() {
    if (!Pathetic.isInitialized())
      throw new IllegalStateException("Pathetic has not been initialized yet");

    init(Pathetic.getPluginInstance());
  }

  public synchronized void increasePathCount() {
    makeSureBStatsIsInitialized();
    paths++;
  }
}
