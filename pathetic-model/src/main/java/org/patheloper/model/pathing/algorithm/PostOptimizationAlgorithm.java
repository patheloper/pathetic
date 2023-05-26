package org.patheloper.model.pathing.algorithm;

import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.model.pathing.pathfinder.AStarPathfinder;
import org.patheloper.model.pathing.result.PathImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * This class implements a post-optimization algorithm for paths. It takes a path to be optimized,
 * reverses it, overlays the original and reversed paths, and interpolates them to find an optimal path.
 *
 * @deprecated This class is deprecated and should not be used. It is a work in progress and has known issues
 *             that may lead to incorrect or unexpected results.
 */
@Deprecated
public class PostOptimizationAlgorithm implements Function<Path, Path> {
    
    private final Pathfinder pathfinder;
    
    public PostOptimizationAlgorithm(PathingRuleSet pathingRuleSet) {
        // TODO: Don't default to AStarPathfinder
        this.pathfinder = new AStarPathfinder(pathingRuleSet.withAsync(false)); // TODO: .withPostOptimization(false)
    }

    @Override
    public Path apply(Path path) {
    
        PathPosition start = path.getStart();
        PathPosition end = path.getEnd();
    
        CompletableFuture<PathfinderResult> result = pathfinder.findPath(end, start).toCompletableFuture();
        if(!result.join().successful())
            return path;
        
        Path reversedPath = result.join().getPath();
        return interpolatePaths(path, reversedPath);
    }

    /**
     * Interpolates the positions from two paths to create an optimized path.
     *
     * @param path1 The first path.
     * @param path2 The second path.
     * @return The optimized path.
     */
    private Path interpolatePaths(Path path1, Path path2) {
        throw new UnsupportedOperationException("This method is not yet implemented.");
    }
}
