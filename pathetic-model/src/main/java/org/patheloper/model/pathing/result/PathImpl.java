package org.patheloper.model.pathing.result;

import com.google.common.collect.Iterables;
import java.util.*;
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

@Value
public class PathImpl implements Path {

  @NonNull
  @Getter(AccessLevel.NONE)
  Iterable<PathPosition> positions;

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
    List<PathPosition> enlargedPositions = interpolatePath(resolution);
    return new PathImpl(start, end, enlargedPositions);
  }

  /**
   * Interpolates the path by adding positions between each consecutive pair of positions.
   *
   * @param resolution the resolution to use for interpolation
   * @return a list of interpolated path positions
   */
  private List<PathPosition> interpolatePath(double resolution) {
    List<PathPosition> interpolatedPositions = new ArrayList<>();
    PathPosition previousPosition = null;

    for (PathPosition currentPosition : positions) {
      if (previousPosition != null) {
        interpolateBetween(previousPosition, currentPosition, resolution, interpolatedPositions);
      }
      interpolatedPositions.add(currentPosition);
      previousPosition = currentPosition;
    }

    return interpolatedPositions;
  }

  /**
   * Interpolates between two path positions, adding interpolated positions to the result list.
   *
   * @param startPosition the starting position
   * @param endPosition the ending position
   * @param resolution the resolution to use for interpolation
   * @param result the list of interpolated positions
   */
  private void interpolateBetween(
      PathPosition startPosition,
      PathPosition endPosition,
      double resolution,
      List<PathPosition> result) {
    int steps = calculateInterpolationSteps(startPosition, endPosition, resolution);

    for (int i = 1; i <= steps; i++) {
      double progress = calculateProgress(i, steps);
      PathPosition interpolatedPosition = interpolatePosition(startPosition, endPosition, progress);
      result.add(interpolatedPosition);
    }
  }

  /**
   * Calculates the number of interpolation steps based on the resolution and distance.
   *
   * @param startPosition the starting position
   * @param endPosition the ending position
   * @param resolution the resolution to use
   * @return the number of interpolation steps
   */
  private int calculateInterpolationSteps(
      PathPosition startPosition, PathPosition endPosition, double resolution) {
    double distance = startPosition.distance(endPosition);
    return (int) Math.ceil(distance / resolution);
  }

  /**
   * Calculates the progress for the current interpolation step.
   *
   * @param currentStep the current step
   * @param totalSteps the total number of steps
   * @return the progress value between 0 and 1
   */
  private double calculateProgress(int currentStep, int totalSteps) {
    return (double) currentStep / totalSteps;
  }

  /**
   * Interpolates between two positions based on the progress value.
   *
   * @param startPosition the starting position
   * @param endPosition the ending position
   * @param progress the progress value between 0 and 1
   * @return the interpolated position
   */
  private PathPosition interpolatePosition(
      PathPosition startPosition, PathPosition endPosition, double progress) {
    return startPosition.interpolate(endPosition, progress);
  }

  @Override
  public Path simplify(double epsilon) {
    validateEpsilon(epsilon);
    Set<PathPosition> simplifiedPositions = simplifyPath(epsilon);
    return new PathImpl(start, end, simplifiedPositions);
  }

  /**
   * Validates the epsilon value for simplification.
   *
   * @param epsilon the epsilon value
   */
  private void validateEpsilon(double epsilon) {
    if (epsilon <= 0 || epsilon >= 1) {
      throw new IllegalArgumentException("Epsilon must be in the range (0, 1)");
    }
  }

  /**
   * Simplifies the path by reducing the number of positions based on the epsilon value.
   *
   * @param epsilon the tolerance for simplification
   * @return a set of simplified path positions
   */
  private Set<PathPosition> simplifyPath(double epsilon) {
    Set<PathPosition> simplifiedPositions = createInitialSimplifiedSet();
    addFilteredPositionsToSimplifiedSet(simplifiedPositions, epsilon);
    return simplifiedPositions;
  }

  /**
   * Creates the initial set of simplified positions, including the start and end positions.
   *
   * @return the initial set of simplified positions
   */
  private Set<PathPosition> createInitialSimplifiedSet() {
    return Stream.of(start, end).collect(Collectors.toSet());
  }

  /**
   * Filters and adds positions to the simplified set based on the epsilon value.
   *
   * @param simplifiedPositions the set to add positions to
   * @param epsilon the epsilon value
   */
  private void addFilteredPositionsToSimplifiedSet(
      Set<PathPosition> simplifiedPositions, double epsilon) {
    int step = calculateEpsilonStep(epsilon);
    filterPositionsIntoSet(simplifiedPositions, step);
  }

  /**
   * Filters positions into the simplified set based on the calculated step size.
   *
   * @param simplifiedPositions the set to add positions to
   * @param step the calculated step size for filtering positions
   */
  private void filterPositionsIntoSet(Set<PathPosition> simplifiedPositions, int step) {
    int index = 0;

    for (PathPosition position : positions) {
      if (index % step == 0) {
        simplifiedPositions.add(position);
      }
      index++;
    }
  }

  /**
   * Calculates the step size for filtering positions based on the epsilon value.
   *
   * @param epsilon the epsilon value
   * @return the step size
   */
  private int calculateEpsilonStep(double epsilon) {
    return (int) Math.max(1, 1.0 / epsilon);
  }

  @Override
  public Path join(Path path) {
    return new PathImpl(start, path.getEnd(), Iterables.concat(positions, path));
  }

  @Override
  public Path trim(int length) {
    Iterable<PathPosition> limitedPositions = trimPath(length);
    return new PathImpl(start, getLastPosition(limitedPositions), limitedPositions);
  }

  /**
   * Trims the path to the specified length.
   *
   * @param length the length to trim the path to
   * @return the trimmed path positions
   */
  private Iterable<PathPosition> trimPath(int length) {
    return Iterables.limit(positions, length);
  }

  /**
   * Retrieves the last position from an iterable of path positions.
   *
   * @param positions the iterable of path positions
   * @return the last position
   */
  private PathPosition getLastPosition(Iterable<PathPosition> positions) {
    return Iterables.getLast(positions);
  }

  @NonNull
  @Override
  public Path mutatePositions(ParameterizedSupplier<PathPosition> mutator) {
    List<PathPosition> mutatedPositions = applyMutatorToPositions(mutator);
    return new PathImpl(
        mutatedPositions.get(0), getLastPositionFromList(mutatedPositions), mutatedPositions);
  }

  /**
   * Applies a mutator function to each position in the path and returns the mutated list of
   * positions.
   *
   * @param mutator the function to mutate positions
   * @return a list of mutated positions
   */
  private List<PathPosition> applyMutatorToPositions(ParameterizedSupplier<PathPosition> mutator) {
    List<PathPosition> mutatedPositions = new LinkedList<>();
    forEachPosition(position -> mutatedPositions.add(mutator.accept(position)));
    return mutatedPositions;
  }

  /**
   * Performs an action for each position in the path.
   *
   * @param action the action to perform on each position
   */
  private void forEachPosition(Consumer<PathPosition> action) {
    for (PathPosition position : positions) {
      action.accept(position);
    }
  }

  /**
   * Retrieves the last position from a list of path positions.
   *
   * @param positions the list of path positions
   * @return the last position
   */
  private PathPosition getLastPositionFromList(List<PathPosition> positions) {
    return positions.get(positions.size() - 1);
  }

  @Override
  public int length() {
    return length;
  }
}
