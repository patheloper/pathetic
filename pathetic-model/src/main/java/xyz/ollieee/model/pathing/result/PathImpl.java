package xyz.ollieee.model.pathing.result;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.NonNull;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public Path interpolate(int nthBlock, double resolution) {
        Iterable<PathLocation> interpolate = SplineHelper.interpolate(new SplineHelper.Spline(locations, nthBlock), resolution);
        return new PathImpl(start, end, interpolate);
    }

    @Override
    public Path join(Path path) {
        return new PathImpl(start, path.getEnd(), Iterables.concat(locations, path.getLocations()));
    }

    @Override
    public Path trim(int length) {
        Iterable<PathLocation> limitedLocations = Iterables.limit(locations, length);
        return new PathImpl(start, Iterables.getLast(limitedLocations), limitedLocations);
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

    private static class SplineHelper {

        static class Spline {

            List<PathLocation> points;

            public Spline(Iterable<PathLocation> points, Integer nthBlock) {
                ArrayList<PathLocation> pointsList = Lists.newArrayList(points);

                this.points = IntStream.range(0, pointsList.size())
                        .filter(n -> n % nthBlock == 0)
                        .mapToObj(pointsList::get)
                        .collect(Collectors.toList());
            }
        }

        private static PathLocation getPoint(float t, Spline path) {
            int p0, p1, p2, p3;

            p1 = ((int) t) + 1;
            p2 = p1 + 1;
            p3 = p2 + 1;
            p0 = p1 - 1;
            t = t - ((int) t);

            double tt = t * t;
            double ttt = tt * t;

            double q1 = -ttt + 2.0f * tt - t;
            double q2 = 3.0f * ttt - 5.0f * tt + 2.0f;
            double q3 = -3.0f * ttt + 4.0f * tt + t;
            double q4 = ttt - tt;

            double tx = 0.5f * (path.points.get(p0).getX() * q1 + path.points.get(p1).getX() * q2 + path.points.get(p2).getX() * q3 + path.points.get(p3).getX() * q4);
            double ty = 0.5f * (path.points.get(p0).getY() * q1 + path.points.get(p1).getY() * q2 + path.points.get(p2).getY() * q3 + path.points.get(p3).getY() * q4);
            double tz = 0.5f * (path.points.get(p0).getZ() * q1 + path.points.get(p1).getZ() * q2 + path.points.get(p2).getZ() * q3 + path.points.get(p3).getZ() * q4);

            return new PathLocation(path.points.get(0).getPathWorld(), tx, ty, tz);
        }

        public static Iterable<PathLocation> interpolate(Spline path, double resolution) {

            LinkedHashSet<PathLocation> locations = new LinkedHashSet<>();

            for (float t = 0; t < path.points.size() - 3; t += resolution) {
                PathLocation pos = getPoint(t, path);
                locations.add(pos);
            }

            return locations;
        }
    }
}
