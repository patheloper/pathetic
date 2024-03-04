package org.patheloper.model.pathing;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.util.ComputingCache;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Node implements Comparable<Node> {

  private static final double MANHATTAN_WEIGHT = 0.3;
  private static final double OCTILE_WEIGHT = 0.15;
  private static final double PERPENDICULAR_WEIGHT = 0.6;
  private static final double HEIGHT_WEIGHT = 0.3;

  private final Integer depth;
  @EqualsAndHashCode.Include private final PathPosition position;
  private final PathPosition target;
  private final PathPosition start;
  private final ComputingCache<Double> heuristic = new ComputingCache<>(this::heuristic);
  private Node parent;

  public Node(PathPosition position, PathPosition start, PathPosition target, Integer depth) {
    this.position = position;
    this.target = target;
    this.start = start;
    this.depth = depth;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public boolean isTarget() {
    return this.position.getBlockX() == target.getBlockX()
        && this.position.getBlockY() == target.getBlockY()
        && this.position.getBlockZ() == target.getBlockZ();
  }

  private double heuristic() {
    double manhattanDistance = this.position.manhattanDistance(target);
    double octileDistance = this.position.octileDistance(target);
    double perpendicularDistance = calculatePerpendicularDistance();
    double heightFactor = Math.abs(this.position.getBlockY() - target.getBlockY()); // Consider height differences

    return (manhattanDistance * MANHATTAN_WEIGHT)
        + (octileDistance * OCTILE_WEIGHT)
        + (perpendicularDistance * PERPENDICULAR_WEIGHT)
        + (heightFactor * HEIGHT_WEIGHT);
  }

  private double calculatePerpendicularDistance() {
    PathVector pathToStart = start.toVector().subtract(position.toVector());
    PathVector pathToTarget = target.toVector().subtract(position.toVector());
    return pathToStart.getCrossProduct(pathToTarget).length() / pathToTarget.length();
  }

  @Override
  public int compareTo(Node o) {
    // This is used in the priority queue to sort the nodes
    return Double.compare(this.heuristic.get(), o.heuristic.get());
  }
}
