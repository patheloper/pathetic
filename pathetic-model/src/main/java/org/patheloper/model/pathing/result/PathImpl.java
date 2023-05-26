package org.patheloper.model.pathing.result;

import com.google.common.collect.Iterables;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.util.ParameterizedSupplier;
import org.patheloper.api.wrapper.PathPosition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Log4j2
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
        try {
            validateEpsilon(epsilon);

            Set<PathPosition> simplifiedPositions = new HashSet<>();
            simplifiedPositions.add(start);
            simplifiedPositions.add(end);

            int index = 0;
            for (PathPosition pathPosition : positions) {
                if (index % (1.0 / epsilon) == 0) {
                    simplifiedPositions.add(pathPosition);
                }
                index++;
            }

            return new PathImpl(start, end, simplifiedPositions);
        } catch (IllegalArgumentException e) {
            log.error("Invalid epsilon value for path simplification", e);
            // Return a default or fallback path if desired
            return this; // Or return a new Path instance, or null, depending on your needs
        }
    }

    private void validateEpsilon(double epsilon) {
        if (epsilon <= 0.0 || epsilon > 1.0) {
            throw new IllegalArgumentException("Epsilon must be in the range of 0.0 to 1.0, inclusive.");
        }
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
}
