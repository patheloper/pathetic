package xyz.oli.api.pathing;

import lombok.NonNull;
import org.bukkit.Location;
import xyz.oli.api.pathing.result.PathfinderResult;
import xyz.oli.api.pathing.strategy.PathfinderStrategy;

import java.util.concurrent.CompletableFuture;

public interface Pathfinder {
    
    /**
     * Tries to find a Path from {@param start} to {@param target} with the set {@link xyz.oli.api.pathing.strategy.PathfinderStrategy} or default {@link xyz.oli.api.pathing.strategy.strategies.DirectPathfinderStrategy}
     */
    PathfinderResult findPath(Location start, Location target);
    
    /**
     * @see #findPath(Location, Location) findPath but async
     */
    CompletableFuture<PathfinderResult> findPathAsync(Location start, Location target);

    /**
     * Sets the strategy to be used
     */
    Pathfinder setStrategy(@NonNull PathfinderStrategy strategy);

    /**
     * Set the max check depth
     */
    Pathfinder setMaxChecks(int maxChecks);

}
