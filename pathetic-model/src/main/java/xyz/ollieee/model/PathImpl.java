package xyz.ollieee.model;

import com.google.common.collect.Iterables;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.wrapper.PathLocation;

@AllArgsConstructor
public class PathImpl implements Path {

    private final PathLocation start;
    private final PathLocation end;
    private final Iterable<PathLocation> locations;

    @Override
    public Path interpolate() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Path join(Path path) {
        return new PathImpl(start, path.getEnd(), Iterables.concat(locations, path.getLocations()));
    }
    
    @NonNull
    @Override
    public Iterable<PathLocation> getLocations() {
        return this.locations;
    }

    @NonNull
    @Override
    public PathLocation getStart() {
        return this.start;
    }

    @NonNull
    @Override
    public PathLocation getEnd() {
        return this.end;
    }
    
}
