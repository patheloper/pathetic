package xyz.ollieee.api.pathing;

import lombok.NonNull;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.concurrent.CompletableFuture;

public interface Pathfinder {
    
    /**
     * Tries to find a Path from {@param start} to {@param target} with the default {@link xyz.ollieee.api.pathing.strategy.strategies.DirectPathfinderStrategy}
     * @return {@link PathfinderResult} the result
     */
    @NonNull
    PathfinderResult findPath(@NonNull PathLocation start, @NonNull PathLocation target);

    /**
     * Tries to find a Path from {@param start} to {@param target} with the set {@link xyz.ollieee.api.pathing.strategy.PathfinderStrategy}
     * @return {@link PathfinderResult} the result
     */
    @NonNull
    PathfinderResult findPath(@NonNull PathLocation start, @NonNull PathLocation target, @NonNull Class<? extends PathfinderStrategy> strategyType);
    
    /**
     * @see #findPath(PathLocation, PathLocation) findPath but async
     * @return {@link CompletableFuture<PathfinderResult>} the future of the result
     */
    @NonNull
    CompletableFuture<PathfinderResult> findPathAsync(@NonNull PathLocation start, @NonNull PathLocation target);

    /**
     * @see #findPath(PathLocation, PathLocation, Class)  - findPath but async
     * @return {@link CompletableFuture<PathfinderResult>} the future of the result
     */
    @NonNull
    CompletableFuture<PathfinderResult> findPathAsync(@NonNull PathLocation start, @NonNull PathLocation target, @NonNull Class<? extends PathfinderStrategy> strategyType);

}
