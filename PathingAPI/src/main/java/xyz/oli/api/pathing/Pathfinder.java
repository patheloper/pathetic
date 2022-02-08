package xyz.oli.api.pathing;

import xyz.oli.api.pathing.result.PathfinderResult;
import xyz.oli.api.wrapper.PathLocation;

import java.util.concurrent.CompletableFuture;

public interface Pathfinder {
    
    PathfinderResult findPath(PathLocation start, PathLocation target);
    
    CompletableFuture<PathfinderResult> findPathAsync(PathLocation start, PathLocation target);
    
}
