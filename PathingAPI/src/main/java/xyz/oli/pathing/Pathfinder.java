package xyz.oli.pathing;

import xyz.oli.wrapper.PathLocation;

import java.util.concurrent.CompletableFuture;

/* maybe make it an abstract class so we can pass Options etc. via constructor */
public interface Pathfinder {
    
    PathfinderResult findPath(PathLocation start, PathLocation target, PathfinderStrategy strategy);
    
    CompletableFuture<PathfinderResult> findPathAsync(PathLocation start, PathLocation target, PathfinderStrategy strategy);
    
}
