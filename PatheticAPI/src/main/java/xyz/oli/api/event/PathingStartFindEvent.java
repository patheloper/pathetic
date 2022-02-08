package xyz.oli.api.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;

import xyz.oli.api.pathing.strategy.PathfinderStrategy;
import xyz.oli.api.wrapper.PathLocation;

/**
 * An event called when a Pathfinder starts pathing.
 */
public class PathingStartFindEvent extends PathingEvent implements Cancellable {

    @Setter
    @Getter
    private boolean cancelled = false;

    @Getter
    private final PathLocation start;

    @Getter
    private final PathLocation target;

    @Getter
    private final PathfinderStrategy strategy;

    public PathingStartFindEvent(PathLocation start, PathLocation target, PathfinderStrategy strategy) {
        this.start = start;
        this.target = target;
        this.strategy = strategy;
    }
}
