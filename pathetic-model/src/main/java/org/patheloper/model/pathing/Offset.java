package org.patheloper.model.pathing;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.patheloper.api.wrapper.PathVector;

/**
 * The {@code Offset} enum represents different sets of directional offsets used in pathfinding
 * algorithms. Each enum constant defines a set of {@link PathVector} objects that correspond to
 * specific movement directions in 3D space.
 */
@Getter
@AllArgsConstructor
public enum Offset {

  /**
   * Represents vertical and horizontal movement vectors (i.e., along the X, Y, and Z axes without
   * diagonal movement).
   */
  ORTHOGONAL(
      Arrays.asList(
          new PathVector(1, 0, 0),
          new PathVector(-1, 0, 0),
          new PathVector(0, 0, 1),
          new PathVector(0, 0, -1),
          new PathVector(0, 1, 0),
          new PathVector(0, -1, 0))),

  /**
   * Represents diagonal movement vectors, which allow for diagonal travel along all three axes in
   * addition to orthogonal movement.
   */
  DIAGONAL(
      Arrays.asList(
          new PathVector(-1, 0, -1),
          new PathVector(-1, 0, 0),
          new PathVector(-1, 0, 1),
          new PathVector(0, 0, -1),
          new PathVector(0, 0, 0),
          new PathVector(0, 0, 1),
          new PathVector(1, 0, -1),
          new PathVector(1, 0, 0),
          new PathVector(1, 0, 1),
          new PathVector(-1, 1, -1),
          new PathVector(-1, 1, 0),
          new PathVector(-1, 1, 1),
          new PathVector(0, 1, -1),
          new PathVector(0, 1, 0),
          new PathVector(0, 1, 1),
          new PathVector(1, 1, -1),
          new PathVector(1, 1, 0),
          new PathVector(1, 1, 1),
          new PathVector(-1, -1, -1),
          new PathVector(-1, -1, 0),
          new PathVector(-1, -1, 1),
          new PathVector(0, -1, -1),
          new PathVector(0, -1, 0),
          new PathVector(0, -1, 1),
          new PathVector(1, -1, -1),
          new PathVector(1, -1, 0),
          new PathVector(1, -1, 1))),

  /**
   * Represents a merged set of all movement vectors, combining both orthogonal and diagonal
   * movement directions.
   */
  ALL_DIRECTIONS(mergeVectors(ORTHOGONAL, DIAGONAL));

  private final List<PathVector> vectors;

  /**
   * Merges vectors from multiple {@code Offset} enum constants into a single list.
   *
   * @param offsets the {@code Offset} constants to merge vectors from
   * @return a combined list of {@link PathVector} objects
   */
  private static List<PathVector> mergeVectors(Offset... offsets) {
    return Arrays.stream(offsets)
        .flatMap(offset -> offset.getVectors().stream())
        .collect(Collectors.toList());
  }
}
