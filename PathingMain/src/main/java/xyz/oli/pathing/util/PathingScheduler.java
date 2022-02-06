package xyz.oli.pathing.util;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import xyz.oli.pathing.PathfindingPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PathingScheduler {

    private static PathfindingPlugin plugin = null;
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

    public static void setPlugin(PathfindingPlugin plugin) {
        PathingScheduler.plugin = plugin;
    }

    public static <T> void runAsync(@NonNull Supplier<T> supplier, @NonNull Consumer<T> callback) {
        CompletableFuture.supplyAsync(supplier, FORK_JOIN_POOL).thenAccept(callback);
    }

    public static <T> CompletableFuture<T> supplyAsync(@NonNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, FORK_JOIN_POOL);
    }

    public static BukkitTask runOnMain(@NonNull Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public static BukkitTask runLaterOnMain(@NonNull Runnable runnable, @NonNull Long delay) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public static BukkitTask runLaterAsync(@NonNull Runnable runnable, @NonNull Long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }
}
