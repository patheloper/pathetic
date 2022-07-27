package xyz.ollieee.model.pathing.result;

import com.google.common.collect.Iterables;
import lombok.NonNull;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.wrapper.PathLocation;

public class PathImpl implements Path {

    private final PathLocation start;
    private final PathLocation end;
    private final Iterable<PathLocation> locations;
    private final int length;

    public PathImpl(PathLocation start, PathLocation end, Iterable<PathLocation> locations) {
        this.start = start;
        this.end = end;
        this.locations = locations;
        this.length = Iterables.size(locations);
    }

    @Override
    public Path interpolate() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Path join(Path path) {
        return new PathImpl(start, path.getEnd(), Iterables.concat(locations, path.getLocations()));
    }

    @Override
    public Path trim(int length) {
        return new PathImpl(start, end, Iterables.limit(locations, length));
    }

    @Override
    public int length() {
        return length;
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

    @NonNull
    @Override
    public Iterable<PathLocation> getLocations() {
        return this.locations;
    }

}
