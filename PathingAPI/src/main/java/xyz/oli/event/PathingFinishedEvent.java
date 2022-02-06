package xyz.oli.event;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Location;

import xyz.oli.wrapper.PathLocation;

import java.util.List;

public class PathingFinishedEvent extends PathingEvent {

    @Getter
    PathLocation start;

    @Getter
    PathLocation target;

    @Getter
    List<Location> locations;

    public PathingFinishedEvent(PathLocation start, PathLocation target, List<Location> locations) {
        this.start = start;
        this.target = target;
        this.locations = ImmutableList.copyOf(locations);
    }
}
