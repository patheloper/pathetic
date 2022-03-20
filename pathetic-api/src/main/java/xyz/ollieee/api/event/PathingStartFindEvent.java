package xyz.ollieee.api.event;

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
    private final PathfinderStrategy strategy;

    public PathingStartFindEvent(PathLocation start, PathLocation target, PathfinderStrategy strategy) {
        this.start = start;
        this.target = target;
        this.strategy = strategy;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Gets the start location
     * @return {@link PathLocation} the start location
     */
    public PathLocation getStart() {
        return this.start;
    }

    /**
     * Gets the target location
     * @return {@link PathLocation} the target location
     */
    public PathLocation getTarget() {
        return this.target;
    }

    /**
     * Gets the pathfinder strategy
     * @return {@link PathfinderStrategy} the strategy
     */
    public PathfinderStrategy getStrategy() {
        return this.strategy;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
