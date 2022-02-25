package xyz.oli.api.pathing;

import lombok.NonNull;
import xyz.oli.api.pathing.result.PathfinderResult;
import xyz.oli.api.pathing.strategy.PathfinderStrategy;
import xyz.oli.api.wrapper.PathLocation;

import java.util.concurrent.CompletableFuture;

public interface Pathfinder {
    
    /**
     * Tries to find a Path from {@param start} to {@param target} with the set {@link xyz.oli.api.pathing.strategy.PathfinderStrategy} or default {@link xyz.oli.api.pathing.strategy.strategies.DirectPathfinderStrategy}
     */
    PathfinderResult findPath(PathLocation start, PathLocation target);
    
    /**
     * @see #findPath(PathLocation, PathLocation) findPath but async
     */
    CompletableFuture<PathfinderResult> findPathAsync(PathLocation start, PathLocation target);

    /**
     * Sets the strategy to be used
     */
    Pathfinder setStrategy(@NonNull PathfinderStrategy strategy);

}
