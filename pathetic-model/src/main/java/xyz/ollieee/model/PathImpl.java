package xyz.ollieee.model;

import lombok.AllArgsConstructor;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.wrapper.PathLocation;

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
