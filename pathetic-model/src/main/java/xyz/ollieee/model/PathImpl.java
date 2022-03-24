package xyz.ollieee.model;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.LinkedHashSet;

@AllArgsConstructor
public class PathImpl implements Path {

    PathLocation start;
    PathLocation end;
    LinkedHashSet<PathLocation> locations;

    @NonNull
    @Override
    public LinkedHashSet<PathLocation> getLocations() {
        return this.locations;
    }

    @NonNull
    @Override
    public PathLocation getStart() {
        return this.start;
    }

    @NonNull
    @Override
    public PathLocation getTarget() {
        return this.end;
    }
}
