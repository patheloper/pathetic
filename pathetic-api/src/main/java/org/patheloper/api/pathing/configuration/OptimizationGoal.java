package org.patheloper.api.pathing.configuration;

/**
 * Defines the optimization goals for the pathfinding algorithm.
 *
 * <p>There are two possible optimization strategies:
 *
 * <ul>
 *   <li>{@link #ACCURACY}: Prioritizes the accuracy of the pathfinding by adding the full path cost
 *       (fCost) to the heuristic calculation. This makes the pathfinder more precise in finding
 *       optimal paths but increases the computational workload, leading to lower performance.
 *   <li>{@link #PERFORMANCE}: Focuses on maximizing performance and speed by simplifying the
 *       pathfinding process, potentially sacrificing some accuracy in favor of faster calculations
 *       and results.
 * </ul>
 */
public enum OptimizationGoal {

  /**
   * Optimizes the algorithm for accurate pathfinding.
   *
   * <p>This mode adds the full path cost (fCost) to the heuristic calculation, which makes the
   * pathfinder more precise in determining the best possible route. However, this increased
   * precision comes at the cost of performance, as the additional calculations lead to more
   * computational load.
   */
  ACCURACY,

  /**
   * Optimizes the algorithm for faster performance.
   */
  PERFORMANCE
}
