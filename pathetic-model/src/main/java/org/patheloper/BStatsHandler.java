package org.patheloper;

import lombok.experimental.UtilityClass;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class BStatsHandler {

    private int paths;

    public void init(JavaPlugin javaPlugin) {
        Metrics metrics = new Metrics(javaPlugin, 20528);
        metrics.addCustomChart(new SingleLineChart("total_paths", () -> {
            int totalPaths = paths;
            paths = 0;
            return totalPaths;
        }));
    }

    public synchronized void increasePathCount() {
        paths++;
    }
}
