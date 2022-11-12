package org.patheloper.api.pathing.result.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.patheloper.api.pathing.result.progress.ProgressMonitor;
import org.patheloper.api.pathing.result.PathfinderResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Represents a pathfinding request to the API
 */
@AllArgsConstructor
public class PathingTask {

    private final CompletableFuture<PathfinderResult> result;
    @Getter
    private final ProgressMonitor progressMonitor;

    /**
     * Adds a callback to be called when the task is completed
     *
     * @param callback the callback to be called
     */
    public void accept(Consumer<PathfinderResult> callback) {
        this.result.thenAccept(callback);
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
}
