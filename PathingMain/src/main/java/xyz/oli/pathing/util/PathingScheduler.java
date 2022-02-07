package xyz.oli.pathing.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Supplier;

@UtilityClass
public class PathingScheduler {

    private JavaPlugin plugin = null;
    private final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);

    public void setPlugin(JavaPlugin plugin) {
        PathingScheduler.plugin = plugin;
    }

    public <T> void runAsync(@NonNull Supplier<T> supplier, @NonNull Consumer<T> callback) {
        CompletableFuture.supplyAsync(supplier, FORK_JOIN_POOL).thenAccept(callback);
    }

    public <T> CompletableFuture<T> supplyAsync(@NonNull Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, FORK_JOIN_POOL);
    }

    public BukkitTask runOnMain(@NonNull Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }

    public BukkitTask runLaterOnMain(@NonNull Runnable runnable, @NonNull Long delay) {
        return Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
    }

    public BukkitTask runLaterAsync(@NonNull Runnable runnable, @NonNull Long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }
}
