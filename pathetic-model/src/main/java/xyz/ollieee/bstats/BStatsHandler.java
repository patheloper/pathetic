package xyz.ollieee.bstats;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class BStatsHandler {
    
    private int pathsCreated = 0;
    private int failedPaths = 0;
    private int lengthOfPaths = 0;
    
    public void init(JavaPlugin plugin) {
        
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
        metrics.addCustomChart(new Metrics.SingleLineChart("length_of_pathfinding_in_blocks", () -> {
            int length = lengthOfPaths;
            lengthOfPaths = 0;
            return length;
        }));
    }
    
    public void increasePathCount() {
        pathsCreated++;
    }
    
    public void increaseFailedPathCount() {
        failedPaths++;
    }
    
    public void addLength(final int value) {
        lengthOfPaths += value;
    }
}