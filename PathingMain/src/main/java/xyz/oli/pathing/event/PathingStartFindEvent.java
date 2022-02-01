package xyz.oli.pathing.event;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.bukkit.event.Cancellable;

import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;
import xyz.oli.pathing.model.wrapper.PathLocation;

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
