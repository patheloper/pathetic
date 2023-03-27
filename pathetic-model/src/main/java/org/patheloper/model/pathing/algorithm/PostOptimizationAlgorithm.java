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

public class PostOptimizationAlgorithm implements Function<Path, Path> {
    
    private final Pathfinder pathfinder;
    
    public PostOptimizationAlgorithm(PathingRuleSet pathingRuleSet) {
        // TODO: Don't default to AStarPathfinder
        this.pathfinder = new AStarPathfinder(pathingRuleSet.withAsync(false).withPostOptimization(false));
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
    
    private Path interpolatePaths(Path path1, Path path2) {
        
        List<PathPosition> positions = new ArrayList<>();
        positions.add(path1.getStart());
    
        // Interpolate positions from path1 and path2
        double totalDistance = path1.length() + path2.length();
        double currentDistance = 0;
        double targetDistance = totalDistance / (path1.length() / path1.getStart().distance(path1.getEnd()));
        double stepDistance = 0.1; // The distance between each interpolated position
        PathPosition lastPosition = path1.getStart();
    
        for (PathPosition position : path1.getPositions()) {
            currentDistance += lastPosition.distance(position);
            while (currentDistance >= targetDistance) {
                double overshot = currentDistance - targetDistance;
                double interpolationRatio = 1 - (overshot / stepDistance);
                PathPosition interpolatedPosition = lastPosition.interpolate(position, interpolationRatio);
                positions.add(interpolatedPosition);
                targetDistance += totalDistance / (path2.length() / stepDistance);
            }
            lastPosition = position;
        }
    
        // Add the last position of path1 and the start position of path2
        positions.add(path1.getEnd());
        positions.add(path2.getStart());
    
        // Interpolate positions from path2
        currentDistance = 0;
        targetDistance = totalDistance / (path2.length() / path2.getStart().distance(path2.getEnd()));
        lastPosition = path2.getStart();
    
        for (PathPosition position : path2.getPositions()) {
            currentDistance += lastPosition.distance(position);
            while (currentDistance >= targetDistance) {
                double overshot = currentDistance - targetDistance;
                double interpolationRatio = overshot / stepDistance;
                PathPosition interpolatedPosition = lastPosition.interpolate(position, interpolationRatio);
                positions.add(interpolatedPosition);
                targetDistance += totalDistance / (path1.length() / stepDistance);
            }
            lastPosition = position;
        }
    
        positions.add(path2.getEnd());
        return new PathImpl(path1.getStart(), path2.getEnd(), positions);
    }
}
