package org.patheloper.model.pathing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.patheloper.api.pathing.configuration.HeuristicWeights;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.util.ComputingCache;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
public class Node implements Comparable<Node> {

  @EqualsAndHashCode.Include private final PathPosition position;
  private final PathPosition start;
  private final PathPosition target;
  private final HeuristicWeights heuristicWeights;
  private final int depth;

  private final ComputingCache<Double> fCostCache = new ComputingCache<>(this::calculateFCost);
  private final ComputingCache<Double> gCostCache = new ComputingCache<>(this::calculateGCost);
  private final ComputingCache<Double> heuristic = new ComputingCache<>(this::heuristic);

  @Setter private Node parent;

  public boolean isTarget() {
    return this.position.getBlockX() == target.getBlockX()
        && this.position.getBlockY() == target.getBlockY()
        && this.position.getBlockZ() == target.getBlockZ();
  }

  /**
   * Calculates the estimated total cost of the path from the start node to the goal node, passing
   * through this node.
   *
   * @return the estimated total cost (represented by the F-Score)
   */
  public double getFCost() {
    return fCostCache.get();
  }

  /**
   * The accumulated cost (also known as G-Score) from the starting node to the current node. This
   * value represents the actual (known) cost of traversing the path to the current node. It is
   * typically calculated by summing the movement costs from the start node to the current node.
   */
  private double getGCost() {
    return gCostCache.get();
  }

  private double calculateFCost() {
    return getGCost() + heuristic.get();
  }

  private double calculateGCost() {
    if (parent == null) {
      return 0;
    }
    return parent.getGCost() + position.distance(parent.position);
  }

  private double heuristic() {
    double manhattanDistance = this.position.manhattanDistance(target);
    double octileDistance = this.position.octileDistance(target);
    double perpendicularDistance = calculatePerpendicularDistance();
    double heightDifference = Math.abs(this.position.getBlockY() - target.getBlockY());

    double manhattanWeight = heuristicWeights.getManhattanWeight();
    double octileWeight = heuristicWeights.getOctileWeight();
    double perpendicularWeight = heuristicWeights.getPerpendicularWeight();
    double heightWeight = heuristicWeights.getHeightWeight();

    double directionalPenalty = Math.abs(this.position.getBlockY() - start.getBlockY());

    return (manhattanDistance * manhattanWeight)
        + (octileDistance * octileWeight)
        + (perpendicularDistance * perpendicularWeight)
        + (heightDifference * heightWeight)
        + (directionalPenalty * 0.5);
  }

  private double calculatePerpendicularDistance() {
    PathVector a = this.position.toVector();
    PathVector b = this.start.toVector();
    PathVector c = this.target.toVector();
    return a.subtract(b).getCrossProduct(c.subtract(b)).length() / c.subtract(b).length();
  }

  @Override
  public int compareTo(@NonNull Node o) {
    int fCostComparison = Double.compare(this.getFCost(), o.getFCost());
    if (fCostComparison != 0) {
      return fCostComparison;
    }
    int heuristicComparison = Double.compare(this.heuristic.get(), o.heuristic.get());
    if (heuristicComparison != 0) {
      return heuristicComparison;
    }
    return Integer.compare(this.depth, o.depth);
  }
}
