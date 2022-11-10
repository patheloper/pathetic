package xyz.ollieee.api.event;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.wrapper.PathLocation;

/**
 * An event called when a Pathfinder starts pathing.
 */
@RequiredArgsConstructor
public class PathingStartFindEvent extends PathingEvent implements Cancellable {

    @Getter
    private final PathLocation start;
    @Getter
    private final PathLocation target;
    @NonNull
    @Getter
    private final PathfinderStrategy pathfinderStrategy;
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
