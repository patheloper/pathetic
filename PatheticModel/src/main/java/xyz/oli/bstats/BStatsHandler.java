package xyz.oli.bstats;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class BStatsHandler {
    
    private static int pathsCreated = 0;
    private static int failedPaths = 0;
    private static int lengthOfPaths = 0;
    
    public static void init(JavaPlugin plugin) {
        
        Metrics metrics = new Metrics(plugin, 14215);
    
        metrics.addCustomChart(new Metrics.SingleLineChart("paths_created", () -> {
            int paths = pathsCreated;
            pathsCreated = 0;
            return paths;
        }));
        metrics.addCustomChart(new Metrics.SingleLineChart("failed_attempts", () -> {
            int failed = failedPaths;
            failedPaths = 0;
            return failed;
        }));
        metrics.addCustomChart(new Metrics.SingleLineChart("length_of_pathfinding_-_blocks", () -> {
            int length = lengthOfPaths;
            lengthOfPaths = 0;
            return length;
        }));
    }
    
    public static void increasePathCount() {
        pathsCreated++;
    }
    
    public static void increaseFailedPathCount() {
        failedPaths++;
    }
    
    public static void addLength(final int value) {
        lengthOfPaths += value;
    }
}