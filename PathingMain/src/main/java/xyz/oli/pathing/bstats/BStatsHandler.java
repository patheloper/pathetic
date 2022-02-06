package xyz.oli.pathing.bstats;

import xyz.oli.pathing.PathfindingPlugin;

import java.math.BigInteger;

public class BStatsHandler {

    private static int pathsCreated = 0;
    private static int failedPaths = 0;
    private static int lengthOfPaths = 0;

    public BStatsHandler(PathfindingPlugin plugin) {
        Metrics metrics = new Metrics(plugin, 14215);

        metrics.addCustomChart(new Metrics.SingleLineChart("paths_created", () -> pathsCreated));
        metrics.addCustomChart(new Metrics.SingleLineChart("failed_attempts", () -> failedPaths));
        metrics.addCustomChart(new Metrics.SingleLineChart("length_of_pathfinding_-_blocks", () -> lengthOfPaths));
    }

    public static void addPath() {
        pathsCreated++;
    }

    public static void addFailedPath() {
        failedPaths++;
    }

    public static void addLength(final int value) {
        lengthOfPaths += value;
    }
}