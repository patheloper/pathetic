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
    private final Class<? extends PathfinderStrategy> strategyType;

    public PathingStartFindEvent(PathLocation start, PathLocation target, Class<? extends PathfinderStrategy> strategyType) {
        this.start = start;
        this.target = target;
        this.strategyType = strategyType;
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
     * Gets the pathfinder strategy type
     * @return {@link PathfinderStrategy} the strategy
     */
    public Class<? extends PathfinderStrategy> getStrategyType() {
        return strategyType;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
