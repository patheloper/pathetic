package xyz.oli.pathing;

import xyz.oli.pathing.model.path.finder.Pathfinder;
import xyz.oli.pathing.model.path.finder.PathfinderResult;
import xyz.oli.pathing.model.wrapper.BukkitConverter;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PathFinder {

    private final Pathfinder pathFinder;

    PathFinder() {
        this.pathFinder = new Pathfinder();
    }

    public void findPath(@NotNull PathfinderOptions pathfinderOptions, @Nullable Consumer<PathfinderResult> callback) {
        if (pathfinderOptions.isAsyncMode()) {
            CompletableFuture.supplyAsync( () ->
                            pathFinder.findPath(BukkitConverter.toPathLocation(pathfinderOptions.getStart()), BukkitConverter.toPathLocation(pathfinderOptions.getTarget()), pathfinderOptions.getStrategy()),
                    PathingAPI.FORK_JOIN_POOL).thenAccept(pathResult -> {
                if (callback != null) callback.accept(pathResult);
            });
            return;
        }

        PathfinderResult pathResult = pathFinder.findPath(BukkitConverter.toPathLocation(pathfinderOptions.getStart()), BukkitConverter.toPathLocation(pathfinderOptions.getTarget()), pathfinderOptions.getStrategy());
        if (callback != null) callback.accept(pathResult);
    }
}
