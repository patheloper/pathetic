package xyz.oli.pathing.util;

import lombok.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PathingScheduler {

    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

    public static <T> void runAsync(@NonNull Supplier<T> supplier, @NonNull Consumer<T> callback) {
        CompletableFuture.supplyAsync(supplier, FORK_JOIN_POOL).thenAccept(callback);
    }

    public static <T> CompletableFuture<T> supplyAsync(@NonNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, FORK_JOIN_POOL);
    }
}
