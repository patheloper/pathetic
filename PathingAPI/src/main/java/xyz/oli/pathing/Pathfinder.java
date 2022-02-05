package xyz.oli.pathing;

import lombok.NonNull;
import org.bukkit.Location;
import xyz.oli.options.PathfinderOptions;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface Pathfinder {

    void findPath(@NonNull PathfinderOptions pathfinderOptions, @NonNull Consumer<PathResult> callback);

    CompletableFuture<PathResult> findPath(@NonNull Location startLocation, @NonNull Location targetLocation);

    CompletableFuture<PathResult> findPath(@NonNull Location startLocation, @NonNull Location targetLocation, @NonNull PathfinderStrategy strategy);

    PathResult findPathNow(@NonNull Location startLocation, @NonNull Location targetLocation);

    PathResult findPathNow(@NonNull Location startLocation, @NonNull Location targetLocation, @NonNull PathfinderStrategy strategy);
}
