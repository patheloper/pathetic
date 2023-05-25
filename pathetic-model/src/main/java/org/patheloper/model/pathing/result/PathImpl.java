package org.patheloper.model.pathing.result;

import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.NonNull;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.util.ParameterizedSupplier;
import org.patheloper.api.wrapper.PathPosition;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    public Path interpolate(double resolution) {

        List<PathPosition> enlargedPositions = new ArrayList<>();

        PathPosition previousPosition = null;
        for (PathPosition position : positions) {

            if (previousPosition != null) {

                double distance = previousPosition.distance(position);
                int steps = (int) Math.ceil(distance / resolution);

                for (int i = 1; i <= steps; i++) {

                    double progress = (double) i / steps;
                    PathPosition interpolatedPosition = previousPosition.interpolate(position, progress);

                    enlargedPositions.add(interpolatedPosition);
                }
            }

            enlargedPositions.add(position);
            previousPosition = position;
        }

        return new PathImpl(start, end, enlargedPositions);
    }

    @Override
    public Path simplify(double epsilon) {

        List<PathPosition> originalPositions = new ArrayList<>();
        positions.forEach(originalPositions::add);

        List<PathPosition> simplifiedPositions = new ArrayList<>();
        simplifyRecursive(originalPositions, 0, originalPositions.size() - 1, epsilon, simplifiedPositions);

        return new PathImpl(start, end, simplifiedPositions);
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

    /**
     * Recursive helper method to simplify positions using the Ramer-Douglas-Peucker algorithm.
     */
    private void simplifyRecursive(List<PathPosition> positions, int start, int end, double epsilon, List<PathPosition> simplifiedPositions) {

        // Find the position with the maximum distance
        double maxDistance = 0;
        int maxDistanceIndex = 0;

        for (int i = start + 1; i < end; i++) {
            double distance = getDistance(positions.get(i), positions.get(start), positions.get(end));
            if (distance > maxDistance) {
                maxDistance = distance;
                maxDistanceIndex = i;
            }
        }

        // If the maximum distance is greater than epsilon, recursively simplify the two subpaths
        if (maxDistance > epsilon) {

            simplifyRecursive(positions, start, maxDistanceIndex, epsilon, simplifiedPositions);
            simplifiedPositions.add(positions.get(maxDistanceIndex));

            simplifyRecursive(positions, maxDistanceIndex, end, epsilon, simplifiedPositions);
        }
    }

    /**
     * Calculates the perpendicular distance between a position and a line segment.
     */
    private double getDistance(PathPosition position, PathPosition lineStart, PathPosition lineEnd) {

        double x = position.getX();
        double y = position.getY();
        double startX = lineStart.getX();
        double startY = lineStart.getY();
        double endX = lineEnd.getX();
        double endY = lineEnd.getY();

        double numerator = Math.abs((endY - startY) * x - (endX - startX) * y + endX * startY - endY * startX);
        double denominator = Math.sqrt(Math.pow(endY - startY, 2) + Math.pow(endX - startX, 2));

        return numerator / denominator;
    }
}
