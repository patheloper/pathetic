package xyz.ollieee.api.event;

import lombok.NonNull;
import org.bukkit.event.Cancellable;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * An event called when a Pathfinder starts pathing.
 */
public class PathingStartFindEvent extends PathingEvent implements Cancellable {

    @NonNull
    private final PathfinderStrategy pathfinderStrategy;
    private final PathLocation start;
    private final PathLocation target;

    private boolean cancelled = false;

    public PathingStartFindEvent(PathLocation start, PathLocation target, @NonNull PathfinderStrategy pathfinderStrategy) {

        this.start = start;
        this.target = target;
        this.pathfinderStrategy = pathfinderStrategy;
    }

    @Override
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

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
