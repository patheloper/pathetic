package xyz.ollieee.api.pathing;

import lombok.NonNull;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.concurrent.CompletableFuture;

public interface Pathfinder {
    
    /**
     * Tries to find a Path from {@param start} to {@param target} with the set {@link xyz.ollieee.api.pathing.strategy.PathfinderStrategy} or default {@link xyz.ollieee.api.pathing.strategy.strategies.DirectPathfinderStrategy}
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
