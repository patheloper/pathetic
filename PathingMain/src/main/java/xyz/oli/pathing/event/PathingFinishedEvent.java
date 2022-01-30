package xyz.oli.pathing.event;

import com.google.common.collect.ImmutableList;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import xyz.oli.pathing.model.wrapper.PathLocation;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public class PathingFinishedEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final PathLocation start;
    private final PathLocation target;
    private final List<Location> locations;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public PathingFinishedEvent(PathLocation start, PathLocation target, List<Location> locations) {
        this.start = start;
        this.target = target;
        this.locations = ImmutableList.copyOf(locations);
    }

    public PathLocation getStart() {
        return this.start.clone();
    }

    public PathLocation getTarget() {
        return this.target.clone();
    }

    public List<Location> getLocations() {
        return this.locations;
    }
}
