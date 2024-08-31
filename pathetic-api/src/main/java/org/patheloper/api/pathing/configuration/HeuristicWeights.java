package org.patheloper.api.pathing.configuration;

import lombok.Value;

/**
 * Represents a set of weights used to calculate a heuristic for the A* pathfinding algorithm. These
 * weights influence the prioritization of different path characteristics during the search.
 *
 * <p>This class defines weights for the following distance metrics:
 *
 * <ul>
 *   <li><b>Manhattan Distance:</b> Prioritizes direct movement along axes.
 *   <li><b>Octile Distance:</b> Allows for diagonal movement for finer-grained pathing.
 *   <li><b>Perpendicular Distance:</b> Penalizes deviation from the straight line to the target,
 *       aiding in smoother paths.
 *   <li><b>Height Difference:</b> Factors in elevation changes when calculating path costs.
 * </ul>
 */
@Value(staticConstructor = "create")
public class HeuristicWeights {

  /**
   * Provides a set of default heuristic weights that may be suitable for natural pathfinding. These
   * values can be adjusted for specific scenarios.
   */
  public static final HeuristicWeights NATURAL_PATH_WEIGHTS = create(0.3, 0.15, 0.6, 0.3);

  /**
   * Provides a set of weights strongly prioritizing the shortest direct path, even if diagonally.
   */
  public static final HeuristicWeights DIRECT_PATH_WEIGHTS = create(0.6, 0.3, 0.0, 0.1);

  /**
   * The weight applied to the Manhattan distance component of the heuristic. A higher weight
   * favours paths with a greater emphasis on direct, axis-aligned movement.
   */
  double manhattanWeight;

  /**
   * The weight applied to the Octile distance component of the heuristic. A higher weight allows
   * diagonal movement, enabling more flexible paths in 3D environments.
   */
  double octileWeight;

  /**
   * The weight applied to the perpendicular distance component of the heuristic. Increased weight
   * discourages deviations from the straight line between the start and target, resulting in
   * smoother paths.
   */
  double perpendicularWeight;

  /**
   * The weight applied to the height difference (elevation change) component of the heuristic. A
   * higher weight gives more consideration to vertical distance, important for terrains with
   * varying verticality.
   */
  double heightWeight;
}
