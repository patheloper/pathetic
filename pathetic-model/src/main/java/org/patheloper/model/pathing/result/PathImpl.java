package org.patheloper.model.pathing.result;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.NonNull;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.util.ParameterizedSupplier;
import org.patheloper.api.wrapper.PathLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PathImpl implements Path {

    private final Iterable<PathLocation> locations;
    private final PathLocation start;
    private final PathLocation end;

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

    @NonNull
    @Override
    public Path mutateLocations(ParameterizedSupplier<PathLocation> mutator) {

        List<PathLocation> locationList = new LinkedList<>();
        for (PathLocation location : this.locations)
            locationList.add(mutator.accept(location));

        return new PathImpl(locationList.get(0), locationList.get(locationList.size() - 1), locationList);
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

    private static final class SplineHelper {

        public static Iterable<PathLocation> interpolate(Spline path, double resolution) {

            if(path.getPoints().isEmpty())
                return Collections.emptyList();

            LinkedHashSet<PathLocation> locations = new LinkedHashSet<>();

            for (float t = 0; t < path.getPoints().size() - 3; t = (float) (t + resolution)) {
                PathLocation pos = getPoint(t, path);
                locations.add(pos);
            }

            return Collections.unmodifiableSet(locations);
        }

        private static PathLocation getPoint(float t, Spline path) {

            int p0 = (int) t;
            int p1 = (int) t + 1;
            int p2 = p1 + 1;
            int p3 = p1 + 2;

            float v = t - (int) t;

            double tt = v * v;
            double ttt = tt * v;

            double q1 = -ttt + 2.0f * tt - v;
            double q2 = 3.0f * ttt - 5.0f * tt + 2.0f;
            double q3 = -3.0f * ttt + 4.0f * tt + v;
            double q4 = ttt - tt;

            double tx = 0.5f * (path.getPoints().get(p0).getX() * q1 + path.getPoints().get(p1).getX() * q2 + path.getPoints().get(p2).getX() * q3 + path.getPoints().get(p3).getX() * q4);
            double ty = 0.5f * (path.getPoints().get(p0).getY() * q1 + path.getPoints().get(p1).getY() * q2 + path.getPoints().get(p2).getY() * q3 + path.getPoints().get(p3).getY() * q4);
            double tz = 0.5f * (path.getPoints().get(p0).getZ() * q1 + path.getPoints().get(p1).getZ() * q2 + path.getPoints().get(p2).getZ() * q3 + path.getPoints().get(p3).getZ() * q4);

            return new PathLocation(path.getPoints().get(0).getPathWorld(), tx, ty, tz);
        }

        private SplineHelper() {
        }

        static class Spline {

            private List<PathLocation> points;

            public Spline(Iterable<PathLocation> points, Integer nthBlock) {
                ArrayList<PathLocation> pointsList = Lists.newArrayList(points);

                this.setPoints(IntStream.range(0, pointsList.size())
                        .filter(n -> n % nthBlock == 0)
                        .mapToObj(pointsList::get)
                        .collect(Collectors.toList()));

                this.getPoints().addAll(Collections.nCopies(3, Iterables.getLast(points)));
                this.getPoints().addAll(0, Collections.nCopies(3, Iterables.getFirst(points, null)));
            }

            public List<PathLocation> getPoints() {
                return points;
            }

            public void setPoints(List<PathLocation> points) {
                this.points = points;
            }
        }
    }
}
