package xyz.oli.backlog;

import lombok.NonNull;
import xyz.oli.pathing.model.path.finder.Pathfinder;
import xyz.oli.pathing.model.path.finder.PathfinderResult;
import xyz.oli.wrapper.BukkitConverter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PathFinder {

    private final Pathfinder pathFinder;

    public PathFinder() {
        this.pathFinder = new Pathfinder(); // we don't want this
    }

    public void findPath(@NonNull PathfinderOptions pathfinderOptions, @NonNull Consumer<PathfinderResult> callback) {
        if (pathfinderOptions.isAsyncMode()) {
            CompletableFuture.supplyAsync( () ->
                            pathFinder.findPath(BukkitConverter.toPathLocation(pathfinderOptions.getStart()), BukkitConverter.toPathLocation(pathfinderOptions.getTarget()), pathfinderOptions.getStrategy()),
                    PathingAPI.FORK_JOIN_POOL).thenAccept(callback);
            return;
        }

        PathfinderResult pathResult = pathFinder.findPath(BukkitConverter.toPathLocation(pathfinderOptions.getStart()), BukkitConverter.toPathLocation(pathfinderOptions.getTarget()), pathfinderOptions.getStrategy());
        callback.accept(pathResult);
    }
}
