package xyz.ollieee.api.pathing.result.task;

import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.result.progress.ProgressMonitor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Represents a pathfinding request to the API
 */
public class PathingTask {

    private final ProgressMonitor progressMonitor;
    private final CompletableFuture<PathfinderResult> result;

    public PathingTask(CompletableFuture<PathfinderResult> result, ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
        this.result = result;
    }

    /**
     * Whether the task is completed
     *
     * @return true if the task is completed
     */
    public boolean isFinished() {
        return this.result.isDone();
    }

    /**
     * Gets the result of the task
     * This is a blocking call
     *
     * @return the result of the task
     */
    public PathfinderResult getResult() {
        return this.result.join();
    }

    /**
     * Adds a callback to be called when the task is completed
     *
     * @param callback the callback to be called
     */
    public void accept(Consumer<PathfinderResult> callback) {
        this.result.thenAccept(callback);
    }

    public ProgressMonitor getProgressMonitor() {
        return this.progressMonitor;
    }
}
