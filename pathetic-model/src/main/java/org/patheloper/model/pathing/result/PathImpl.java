package org.patheloper.model.pathing.result;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.util.ParameterizedSupplier;
import org.patheloper.api.wrapper.PathPosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PathImpl implements Path {

    @NonNull
    @Getter
    private final Iterable<PathPosition> positions;
    @NonNull
    @Getter
    private final PathPosition start;
    @NonNull
    @Getter
    private final PathPosition end;

    private final int length;

    public PathImpl(@NonNull PathPosition start, @NonNull PathPosition end, @NonNull Iterable<@NonNull PathPosition> positions) {

        this.start = start;
        this.end = end;
        this.positions = positions;

        this.length = Iterables.size(positions);
    }

    @Override
    public Path interpolate(int nthBlock, double resolution) {
        Iterable<PathPosition> interpolate = SplineHelper.interpolate(new SplineHelper.Spline(positions, nthBlock), resolution);
        return new PathImpl(start, end, interpolate);
    }

    @Override
    public Path join(Path path) {
        return new PathImpl(start, path.getEnd(), Iterables.concat(positions, path.getPositions()));
    }

    @Override
    public Path trim(int length) {
        Iterable<PathPosition> limitedPositions = Iterables.limit(positions, length);
        return new PathImpl(start, Iterables.getLast(limitedPositions), limitedPositions);
    }

    @NonNull
    @Override
    public Path mutatePositions(ParameterizedSupplier<PathPosition> mutator) {

        List<PathPosition> positionList = new LinkedList<>();
        for (PathPosition position : this.positions)
            positionList.add(mutator.accept(position));

        return new PathImpl(positionList.get(0), positionList.get(positionList.size() - 1), positionList);
    }

    @Override
    public int length() {
        return length;
    }

    private static final class SplineHelper {

        public static Iterable<PathPosition> interpolate(Spline path, double resolution) {

            if(path.points.isEmpty())
                return Collections.emptyList();

            LinkedHashSet<PathPosition> pathPositions = new LinkedHashSet<>();

            for (float t = 0; t < path.points.size() - 3; t = (float) (t + resolution)) {
                PathPosition pos = getPoint(t, path);
                pathPositions.add(pos);
            }

            return Collections.unmodifiableSet(pathPositions);
        }

        private static PathPosition getPoint(float t, Spline path) {

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

            double tx = 0.5f * (path.points.get(p0).getX() * q1 + path.points.get(p1).getX() * q2 + path.points.get(p2).getX() * q3 + path.points.get(p3).getX() * q4);
            double ty = 0.5f * (path.points.get(p0).getY() * q1 + path.points.get(p1).getY() * q2 + path.points.get(p2).getY() * q3 + path.points.get(p3).getY() * q4);
            double tz = 0.5f * (path.points.get(p0).getZ() * q1 + path.points.get(p1).getZ() * q2 + path.points.get(p2).getZ() * q3 + path.points.get(p3).getZ() * q4);

            return new PathPosition(path.points.get(0).getPathDomain(), tx, ty, tz);
        }

        private SplineHelper() {
        }

        static class Spline {

            List<PathPosition> points;

            public Spline(Iterable<PathPosition> points, Integer nthBlock) {
                ArrayList<PathPosition> pointsList = Lists.newArrayList(points);

                this.points = IntStream.range(0, pointsList.size())
                        .filter(n -> n % nthBlock == 0)
                        .mapToObj(pointsList::get)
                        .collect(Collectors.toList());

                this.points.addAll(Collections.nCopies(3, Iterables.getLast(points)));
                this.points.addAll(0, Collections.nCopies(3, Iterables.getFirst(points, null)));
            }
        }
    }
}
