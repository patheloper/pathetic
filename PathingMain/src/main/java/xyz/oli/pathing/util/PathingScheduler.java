package xyz.oli.pathing.util;

import lombok.NonNull;
import xyz.oli.pathing.PathResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PathingScheduler {

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

    public static void runAsync(Supplier<PathResult> supplier, @NonNull Consumer<PathResult> callback) {
        CompletableFuture.supplyAsync(supplier, FORK_JOIN_POOL).thenAccept(callback);
    }
}
