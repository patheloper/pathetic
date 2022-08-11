package xyz.ollieee.api.event;

import lombok.NonNull;
import org.bukkit.event.Cancellable;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * An event called when a Pathfinder starts pathing.
 * Set it as cancelled to stop the pathfinding attempt
 */
public class PathingStartFindEvent extends PathingEvent implements Cancellable {

    private boolean cancelled = false;
    private final PathLocation start;
    private final PathLocation target;
    @NonNull
    private final PathfinderStrategy pathfinderStrategy;

    public PathingStartFindEvent(PathLocation start, PathLocation target, @NonNull PathfinderStrategy pathfinderStrategy) {
        this.start = start;
        this.target = target;
        this.pathfinderStrategy = pathfinderStrategy;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public PathLocation getStart() {
        return this.start;
    }

    public PathLocation getTarget() {
        return this.target;
    }

    public @NonNull PathfinderStrategy getPathfinderStrategy() {
        return this.pathfinderStrategy;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
