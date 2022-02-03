package xyz.oli.event;

import com.google.common.collect.ImmutableList;
import lombok.Value;
import org.bukkit.Location;

import xyz.oli.wrapper.PathLocation;

import java.util.List;

@Value
public class PathingFinishedEvent extends PathingEvent {

    PathLocation start;
    PathLocation target;
    List<Location> locations;

    public PathingFinishedEvent(PathLocation start, PathLocation target, List<Location> locations) {
        this.start = start;
        this.target = target;
        this.locations = ImmutableList.copyOf(locations);
    }
}
