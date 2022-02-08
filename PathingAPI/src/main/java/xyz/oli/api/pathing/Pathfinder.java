package xyz.oli.api.pathing;

import xyz.oli.api.pathing.options.PathfinderOptions;
import xyz.oli.api.pathing.result.PathfinderResult;

import java.util.concurrent.CompletableFuture;

public abstract class Pathfinder {
    
    protected final PathfinderOptions options;
    
    protected Pathfinder(PathfinderOptions options) {
        this.options = options;
    }
    
    public abstract PathfinderResult findPath();
    
    public abstract CompletableFuture<PathfinderResult> findPathAsync();
    
}
