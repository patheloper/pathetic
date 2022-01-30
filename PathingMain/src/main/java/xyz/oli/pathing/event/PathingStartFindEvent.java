package xyz.oli.pathing.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;
import xyz.oli.pathing.model.wrapper.PathLocation;

import org.jetbrains.annotations.NotNull;

public class PathingStartFindEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean cancelled = false;
    private final PathLocation start;
    private final PathLocation target;
    private final PathfinderStrategy strategy;

    public PathingStartFindEvent(PathLocation start, PathLocation target, PathfinderStrategy strategy) {
        this.start = start;
        this.target = target;
        this.strategy = strategy;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public PathLocation getStart() {
        return this.start.clone();
    }

    public PathLocation getTarget() {
        return this.target.clone();
    }

    public PathfinderStrategy getStrategy() {
        return this.strategy;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }
}
