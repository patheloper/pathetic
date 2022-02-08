package xyz.oli.pathing.model;

import lombok.AllArgsConstructor;
import xyz.oli.api.pathing.result.Path;
import xyz.oli.api.wrapper.PathLocation;

import java.util.LinkedHashSet;

@AllArgsConstructor
public class PathImpl implements Path {

    PathLocation start;
    PathLocation end;
    LinkedHashSet<PathLocation> locations;

    @Override
    public LinkedHashSet<PathLocation> getLocations() {
        return this.locations;
    }

    @Override
    public PathLocation getStart() {
        return this.start;
    }

    @Override
    public PathLocation getTarget() {
        return this.end;
    }
}
