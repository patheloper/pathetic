package org.patheloper.model.pathing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.patheloper.api.pathing.configuration.HeuristicWeights;
import org.patheloper.api.pathing.configuration.OptimizationGoal;
import org.patheloper.api.pathing.configuration.PathfinderConfiguration;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.util.ComputingCache;

/**
 * Represents a node used in the pathfinding algorithm. A node stores its position, heuristic
 * weights, and references to its parent, start, and target nodes. It also calculates path costs
 * such as F-Cost (total cost) and G-Cost (actual cost from the start).
 */
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Node implements Comparable<Node> {

  /** The current position of the node in the grid. */
  @EqualsAndHashCode.Include private final PathPosition position;

  /** The start position from which the pathfinding begins. */
  private final PathPosition start;

  /** The target position to reach through pathfinding. */
  private final PathPosition target;

  /** Weights used to calculate heuristic estimates for the pathfinding algorithm. */
  private final HeuristicWeights heuristicWeights;

  /**
   * The depth of the node in the search tree, often used to track how far the node is from the
   * start.
   */
  private final int depth;

  /** The configuration for the pathfinder that defines optimization goals and heuristics. */
  private final PathfinderConfiguration pathfinderConfiguration;

  /** Cached F-Cost (estimated total cost) for efficiency. */
  private final ComputingCache<Double> fCostCache = new ComputingCache<>(this::calculateFCost);

  /** Cached G-Cost (actual cost from start to this node) for efficiency. */
  private final ComputingCache<Double> gCostCache = new ComputingCache<>(this::calculateGCost);

  /** Cached heuristic value for efficiency. */
  private final ComputingCache<Double> heuristicCache =
      new ComputingCache<>(this::calculateHeuristic);

  /** The parent node in the path, used to track the path from start to this node. */
  @Setter private Node parent;

  /**
   * Determines if this node is the target.
   *
   * @return {@code true} if this node represents the target position, {@code false} otherwise.
   */
  public boolean isTarget() {
    return position.equals(target);
  }

  /**
   * Gets the F-Cost, which is the total estimated cost of the path through this node. The F-Cost is
   * the sum of the G-Cost (actual cost from the start) and the heuristic estimate to the target.
   *
   * @return the F-Cost.
   */
  public double getFCost() {
    return fCostCache.get();
  }

  /**
   * Gets the G-Cost, which is the actual cost from the start position to this node.
   *
   * @return the G-Cost.
   */
  private double getGCost() {
    return gCostCache.get();
  }

  /**
   * Calculates the F-Cost, which is the sum of the G-Cost and the heuristic estimate to the target.
   *
   * @return the calculated F-Cost.
   */
  private double calculateFCost() {
    return getGCost() + heuristicCache.get();
  }

  /**
   * Calculates the G-Cost, which is the actual cost from the start to this node. If the node has no
   * parent, the cost is zero.
   *
   * @return the calculated G-Cost.
   */
  private double calculateGCost() {
    if (parent == null) {
      return 0;
    }
    return parent.getGCost() + position.distance(parent.position);
  }

  /**
   * Calculates the heuristic estimate from this node to the target. This method delegates the
   * calculation to a {@link HeuristicCalculator}.
   *
   * @return the calculated heuristic value.
   */
  private double calculateHeuristic() {
    return new HeuristicCalculator(start, position, target, heuristicWeights).calculate();
  }

  /**
   * Compares this node to another node based on their F-Cost and heuristic values. If the
   * pathfinder configuration prioritizes accuracy, F-Cost is compared first, otherwise heuristic
   * values are compared.
   *
   * @param other the node to compare against.
   * @return a negative integer, zero, or a positive integer as this node is less than, equal to, or
   *     greater than the specified node.
   */
  @Override
  public int compareTo(@NonNull Node other) {
    if (pathfinderConfiguration.getOptimizationGoal() == OptimizationGoal.ACCURACY) {
      int fCostComparison = Double.compare(getFCost(), other.getFCost());
      if (fCostComparison != 0) return fCostComparison;
    }

    int heuristicComparison = Double.compare(heuristicCache.get(), other.heuristicCache.get());
    if (heuristicComparison != 0) return heuristicComparison;

    return Integer.compare(depth, other.depth);
  }

  /**
   * Calculates the heuristic distance between two nodes using different methods, including
   * Manhattan, octile, perpendicular, and height differences. The calculated heuristic is weighted
   * based on the given heuristic weights.
   */
  private static class HeuristicCalculator {
    private final PathPosition start;
    private final PathPosition position;
    private final PathPosition target;
    private final HeuristicWeights heuristicWeights;

    /**
     * Constructs a heuristic calculator for the given start, current position, target, and weights.
     *
     * @param start the start position of the pathfinding.
     * @param position the current node's position.
     * @param target the target position of the pathfinding.
     * @param heuristicWeights the weights for different heuristic methods.
     */
    HeuristicCalculator(
        PathPosition start,
        PathPosition position,
        PathPosition target,
        HeuristicWeights heuristicWeights) {
      this.start = start;
      this.position = position;
      this.target = target;
      this.heuristicWeights = heuristicWeights;
    }

    /**
     * Calculates the heuristic cost by combining various distance calculations (Manhattan, octile,
     * perpendicular, and height differences) with their corresponding weights.
     *
     * @return the calculated heuristic value.
     */
    double calculate() {
      double manhattan = calculateManhattanDistance();
      double octile = calculateOctileDistance();
      double perpendicular = calculatePerpendicularDistance();
      double heightDiff = calculateHeightDifference();

      return calculateWeightedHeuristic(manhattan, octile, perpendicular, heightDiff);
    }

    /**
     * Calculates the Manhattan distance between the current position and the target.
     *
     * @return the Manhattan distance as a double
     */
    private double calculateManhattanDistance() {
      return position.manhattanDistance(target);
    }

    /**
     * Calculates the Octile distance between the current position and the target.
     *
     * @return the Octile distance as a double
     */
    private double calculateOctileDistance() {
      return position.octileDistance(target);
    }

    /**
     * Calculates the perpendicular distance from the current position to the line connecting the
     * start and target points.
     *
     * @return the perpendicular distance as a double
     */
    private double calculatePerpendicularDistance() {
      return new PerpendicularDistanceCalculator(start, position, target).calculate();
    }

    /**
     * Calculates the absolute height difference between the current position and the target.
     *
     * @return the absolute height difference as a double
     */
    private double calculateHeightDifference() {
      return Math.abs(position.getBlockY() - target.getBlockY());
    }

    /**
     * Calculates a weighted heuristic value using the provided distance metrics.
     *
     * <p>This method combines the Manhattan, Octile, perpendicular distances, and height
     * difference, and applies the respective heuristic weights to compute a final weighted
     * heuristic.
     *
     * @param manhattan the Manhattan distance
     * @param octile the Octile distance
     * @param perpendicular the perpendicular distance
     * @param heightDiff the height difference
     * @return the weighted heuristic value as a double
     */
    private double calculateWeightedHeuristic(
        double manhattan, double octile, double perpendicular, double heightDiff) {
      return Math.max(
          manhattan * heuristicWeights.getManhattanWeight(),
          octile * heuristicWeights.getOctileWeight()
              + perpendicular * heuristicWeights.getPerpendicularWeight()
              + heightDiff * heuristicWeights.getHeightWeight());
    }
  }

  /**
   * Calculates the perpendicular distance between two vectors in space, using vector cross product.
   */
  private static class PerpendicularDistanceCalculator {
    private final PathVector vectorA;
    private final PathVector vectorB;
    private final PathVector vectorC;

    /**
     * Constructs a perpendicular distance calculator for the given positions.
     *
     * @param start the start position.
     * @param current the current node's position.
     * @param target the target position.
     */
    PerpendicularDistanceCalculator(PathPosition start, PathPosition current, PathPosition target) {
      this.vectorA = start.toVector();
      this.vectorB = current.toVector();
      this.vectorC = target.toVector();
    }

    /**
     * Calculates the perpendicular distance using vector mathematics, specifically the cross
     * product.
     *
     * @return the perpendicular distance.
     */
    double calculate() {
      PathVector AB = vectorA.subtract(vectorB);
      PathVector BC = vectorC.subtract(vectorB);
      return AB.getCrossProduct(BC).length() / BC.length();
    }
  }
}
