package xyz.oli.pathing.bstats;

import xyz.oli.pathing.PathfindingPlugin;

public class BStatsHandler {

    private static int pathsCreated = 0;

    public BStatsHandler(PathfindingPlugin plugin) {
        Metrics metrics = new Metrics(plugin, 14215);

        metrics.addCustomChart(new Metrics.SingleLineChart("paths_created", () -> pathsCreated));
    }

    public static void addPath() {
        pathsCreated++;
    }
}
