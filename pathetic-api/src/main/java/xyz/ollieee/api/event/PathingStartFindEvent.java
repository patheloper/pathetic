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
    private final PathfinderStrategy pathfinderStrategy;

    public PathingStartFindEvent(@NonNull PathLocation start, @NonNull PathLocation target, @NonNull PathfinderStrategy pathfinderStrategy) {
        this.start = start;
        this.target = target;
        this.pathfinderStrategy = pathfinderStrategy;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Gets the start location
     * @return {@link PathLocation} the start location
     */
    @NonNull
    public PathLocation getStart() {
        return this.start;
    }

    /**
     * Gets the target location
     * @return {@link PathLocation} the target location
     */
    @NonNull
    public PathLocation getTarget() {
        return this.target;
    }
    
    /**
     * Gets the pathfinder strategy type
     * @return {@link PathfinderStrategy} the strategy
     */
    @NonNull
    public PathfinderStrategy getStrategy() {
        return pathfinderStrategy;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
