package xyz.oli.pathing.api;

import xyz.oli.pathing.model.path.finder.Pathfinder;
import xyz.oli.pathing.model.path.finder.PathfinderResult;
import xyz.oli.pathing.model.wrapper.BukkitConverter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;

public interface Finder {

    ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    Pathfinder pathFinder = new Pathfinder();

    static void findPath(PathfinderOptions options, Consumer<PathfinderResult> callback) {

        if (options.isAsyncMode()) {
            CompletableFuture.supplyAsync( () ->
                    pathFinder.findPath(BukkitConverter.toPathLocation(options.getStart()), BukkitConverter.toPathLocation(options.getTarget()), options.getStrategy()),
                    FORK_JOIN_POOL).thenAccept(pathResult -> {
                if (callback != null) callback.accept(pathResult);
            });
        }

        PathfinderResult pathResult = pathFinder.findPath(BukkitConverter.toPathLocation(options.getStart()), BukkitConverter.toPathLocation(options.getTarget()), options.getStrategy());
        if (callback != null) callback.accept(pathResult);
    }
}
