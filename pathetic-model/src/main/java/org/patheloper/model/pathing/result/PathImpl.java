package org.patheloper.model.pathing.result;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.util.ParameterizedSupplier;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.util.ErrorLogger;

@Value
public class PathImpl implements Path {

  @NonNull @Getter(AccessLevel.NONE) Iterable<PathPosition> positions;
  @NonNull PathPosition start;
  @NonNull PathPosition end;

  int length;

  public PathImpl(
      @NonNull PathPosition start,
      @NonNull PathPosition end,
      @NonNull Iterable<@NonNull PathPosition> positions) {
    this.start = start;
    this.end = end;
    this.positions = positions;
    this.length = Iterables.size(positions);
  }

  @Override
  public @NonNull Iterator<PathPosition> iterator() {
    return positions.iterator();
  }

  @Override
  public void forEach(Consumer<? super PathPosition> action) {
    positions.forEach(action);
  }

  @Override
  public Path interpolate(double resolution) {
    List<PathPosition> enlargedPositions = new ArrayList<>();

    PathPosition previousPosition = null;
    for (PathPosition position : positions) {
      if (previousPosition != null)
        interpolateBetweenPositions(previousPosition, position, resolution, enlargedPositions);

      enlargedPositions.add(position);
      previousPosition = position;
    }

    return new PathImpl(start, end, enlargedPositions);
  }

  private void interpolateBetweenPositions(
      PathPosition startPosition,
      PathPosition endPosition,
      double resolution,
      List<PathPosition> result) {
    double distance = startPosition.distance(endPosition);
    int steps = (int) Math.ceil(distance / resolution);

    for (int i = 1; i <= steps; i++) {
      double progress = (double) i / steps;

      PathPosition interpolatedPosition = startPosition.interpolate(endPosition, progress);
      result.add(interpolatedPosition);
    }
  }

  @Override
  public Path simplify(double epsilon) {
    try {
      validateEpsilon(epsilon);

      Set<PathPosition> simplifiedPositions = Stream.of(start, end).collect(Collectors.toSet());

      simplifiedPositions.addAll(filterPositionsByEpsilon(epsilon));
      return new PathImpl(start, end, simplifiedPositions);
    } catch (IllegalArgumentException e) {
      throw ErrorLogger.logFatalError("Invalid epsilon value for path simplification", e);
    }
  }

  private Set<PathPosition> filterPositionsByEpsilon(double epsilon) {
    Set<PathPosition> filteredPositions = new HashSet<>();

    int index = 0;
    for (PathPosition pathPosition : positions) {
      if (index % (1.0 / epsilon) == 0) {
        filteredPositions.add(pathPosition);
      }
      index++;
    }

    return filteredPositions;
  }

  private void validateEpsilon(double epsilon) {
    if (epsilon <= 0.0 || epsilon > 1.0) {
      throw ErrorLogger.logFatalError("Epsilon must be in the range of 0.0 to 1.0, inclusive");
    }
  }

  @Override
  public Path join(Path path) {
    return new PathImpl(start, path.getEnd(), Iterables.concat(positions, path));
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
    applyMutator(mutator, positionList);
    return new PathImpl(
        positionList.get(0), positionList.get(positionList.size() - 1), positionList);
  }

  @Override
  public int length() {
    return length;
  }

  private void applyMutator(
      ParameterizedSupplier<PathPosition> mutator, List<PathPosition> positionList) {
    for (PathPosition position : this.positions) positionList.add(mutator.accept(position));
  }
}
