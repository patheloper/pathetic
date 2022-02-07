package xyz.oli.pathing;

import lombok.NonNull;
import org.bukkit.Location;
import xyz.oli.options.PathfinderOptions;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Pathfinder {

    /**
     * Finds a path using the PathfinderOptions and taking the callback as an argument
     * @param pathfinderOptions The options
     * @param callback The callback consumer
     */
    void findPathAsync(@NonNull PathfinderOptions pathfinderOptions, @NonNull Consumer<PathResult> callback);

    /**
     * Finds a path between two locations
     * @param startLocation The start location
     * @param targetLocation The target location
     * @return CompletableFuture<PathResult> The CF that will contain the result of the find
     */
    CompletableFuture<PathResult> findPathAsync(@NonNull Location startLocation, @NonNull Location targetLocation);

    /**
     * Finds a path between two locations
     * @param startLocation The start location
     * @param targetLocation The target location
     * @param strategy The strategy to follow
     * @return CompletableFuture<PathResult> The CF that will contain the result of the find
     */
    CompletableFuture<PathResult> findPathAsync(@NonNull Location startLocation, @NonNull Location targetLocation, @NonNull PathfinderStrategy strategy);

    /**
     * Finds a path synchronously and returns the result
     * @param startLocation The start location
     * @param targetLocation The target location
     * @return PathResult - The result of the Pathfinding
     */
    PathResult findPathSync(@NonNull Location startLocation, @NonNull Location targetLocation);

    /**
     * Finds a path synchronously and returns the result
     * @param startLocation The start location
     * @param targetLocation The target location
     * @param strategy The strategy to follow
     * @return PathResult - The result of the Pathfinding
     */
    PathResult findPathSync(@NonNull Location startLocation, @NonNull Location targetLocation, @NonNull PathfinderStrategy strategy);
}
