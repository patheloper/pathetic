package xyz.ollieee.api.event;

import lombok.*;
import org.bukkit.event.Cancellable;

import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * An event called when a Pathfinder starts pathing.
 * Set it as cancelled to stop the pathfinding attempt
 */
@RequiredArgsConstructor
public class PathingStartFindEvent extends PathingEvent implements Cancellable {

    @Setter
    @Getter
    private boolean cancelled = false;

    @Getter
    private final PathLocation start;

    @Getter
    private final PathLocation target;

    @NonNull
    @Getter
    private final PathfinderStrategy pathfinderStrategy;

}
