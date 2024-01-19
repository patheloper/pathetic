package org.patheloper.model.pathing.pathfinder;

import lombok.NonNull;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.PathValidationContext;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.model.pathing.Node;
import org.patheloper.model.pathing.Offset;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.util.ErrorLogger;
import org.patheloper.util.WatchdogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

/** A pathfinder that uses the A* algorithm. */
public class AStarPathfinder extends AbstractPathfinder {

  public AStarPathfinder(PathingRuleSet pathingRuleSet) {
    super(pathingRuleSet);
  }

  @Override
  protected PathfinderResult resolvePath(
      PathPosition start, PathPosition target, PathfinderStrategy strategy) {
    Node startNode = new Node(start.floor(), start.floor(), target.floor(), 0);

    PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));
    Set<PathPosition> examinedPositions = new HashSet<>();

    int depth = 1;
    Node fallbackNode = startNode;

    while (!nodeQueue.isEmpty() && depth <= pathingRuleSet.getMaxIterations()) {
      tickWatchdogIfNeeded(depth);

      Node currentNode = getNextNodeFromQueue(nodeQueue);
      fallbackNode = currentNode;

      if (hasReachedLengthLimit(currentNode)) {
        return finishPathing(PathState.LENGTH_LIMITED, currentNode);
      }

      if (currentNode.isTarget()) {
        return finishPathing(PathState.FOUND, currentNode);
      }

      evaluateNewNodes(
          nodeQueue,
          examinedPositions,
          currentNode,
          strategy,
          this.pathingRuleSet.isAllowingDiagonal());
      depth++;
    }

    return backupPathfindingOrFailure(depth, start, target, strategy, fallbackNode);
  }

  private void tickWatchdogIfNeeded(int depth) {
    if (depth % 500 == 0) {
      WatchdogUtil.tickWatchdog();
    }
  }

  private Node getNextNodeFromQueue(PriorityQueue<Node> nodeQueue) {
    Node currentNode = nodeQueue.poll();
    if (currentNode == null) {
      throw ErrorLogger.logFatalError("A node was null when it shouldn't have been");
    }
    return currentNode;
  }

  private boolean hasReachedLengthLimit(Node currentNode) {
    return pathingRuleSet.getMaxLength() != 0
        && currentNode.getDepth() > pathingRuleSet.getMaxLength();
  }

  private PathfinderResult finishPathing(PathState pathState, Node currentNode) {
    return finishPathing(new PathfinderResultImpl(pathState, fetchRetracedPath(currentNode)));
  }

  /** If the pathfinder has failed to find a path, it will try to still give a result. */
  private PathfinderResult backupPathfindingOrFailure(
      int depth,
      PathPosition start,
      PathPosition target,
      PathfinderStrategy strategy,
      Node fallbackNode) {
    return maxIterations(depth, fallbackNode)
        .or(() -> counterCheck(start, target, strategy))
        .or(() -> fallback(fallbackNode))
        .orElse(
            finishPathing(
                new PathfinderResultImpl(
                    PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET))));
  }

  /** Checks if the pathfinder has reached the maximum number of iterations. */
  private Optional<PathfinderResult> maxIterations(int depth, Node fallbackNode) {
    if (depth > pathingRuleSet.getMaxIterations())
      return Optional.of(
          finishPathing(
              new PathfinderResultImpl(
                  PathState.MAX_ITERATIONS_REACHED, fetchRetracedPath(fallbackNode))));
    return Optional.empty();
  }

  /**
   * Checks if the pathfinder is allowed to fallback to the last node it examined and retraces the
   * path from there.
   */
  private Optional<PathfinderResult> fallback(Node fallbackNode) {
    if (pathingRuleSet.isAllowingFallback())
      return Optional.of(
          finishPathing(
              new PathfinderResultImpl(PathState.FALLBACK, fetchRetracedPath(fallbackNode))));
    return Optional.empty();
  }

  /** Checks if the pathfinder can find a path from the target to the start. */
  private Optional<PathfinderResult> counterCheck(
      PathPosition start, PathPosition target, PathfinderStrategy strategy) {
    if (!pathingRuleSet.isCounterCheck()) {
      return Optional.empty();
    }

    AStarPathfinder aStarPathfinder =
        new AStarPathfinder(PathingRuleSet.deepCopy(pathingRuleSet).withCounterCheck(false));
    PathfinderResult pathfinderResult = aStarPathfinder.resolvePath(target, start, strategy);

    if (pathfinderResult.getPathState() == PathState.FOUND) {
      return Optional.of(pathfinderResult);
    }

    return Optional.empty();
  }

  /**
   * Evaluates new nodes and adds them to the given node queue if they are valid.
   *
   * @param nodeQueue the node queue to add new nodes to
   * @param examinedPositions a set of examined positions
   * @param currentNode the current node
   * @param strategy the pathfinder strategy to use for validating nodes
   * @param allowingDiagonal whether diagonal movement is allowed
   */
  private void evaluateNewNodes(
      Collection<Node> nodeQueue,
      Set<PathPosition> examinedPositions,
      Node currentNode,
      PathfinderStrategy strategy,
      boolean allowingDiagonal) {
    nodeQueue.addAll(
        fetchValidNeighbours(
            nodeQueue, examinedPositions, currentNode, strategy, allowingDiagonal));
  }

  /**
   * Determines whether the given node is valid and can be added to the node queue.
   *
   * @param currentNode the node we are moving from
   * @param newNode the node to validate
   * @param nodeQueue the node queue to check for duplicates
   * @param examinedPositions a set of examined positions
   * @param strategy the pathfinder strategy to use for validating nodes
   * @param allowingDiagonal whether we are allowing diagonal movement
   * @return {@code true} if the node is valid and can be added to the node queue, {@code false}
   *     otherwise
   */
  private boolean isNodeValid(
      Node currentNode,
      Node newNode,
      Collection<Node> nodeQueue,
      Set<PathPosition> examinedPositions,
      PathfinderStrategy strategy,
      boolean allowingDiagonal) {
    if (isNodeInvalid(newNode, nodeQueue, examinedPositions, strategy)) return false;

    /*
     * So at this point there is nothing wrong with the node itself, We can move to it technically.
     * But we need to check if we can move to it from the current node. If we are moving diagonally, we need to check
     * if we can move to the adjacent nodes as well. The cornerCuts represent the offsets from the current node to the
     * adjacent nodes. If we can't move to any of the adjacent nodes, we can't move to the current node either.
     */

    if (!allowingDiagonal) return examinedPositions.add(newNode.getPosition());

    if (!isDiagonalMove(currentNode, newNode)) return examinedPositions.add(newNode.getPosition());

    return isReachable(currentNode, newNode, strategy)
        && examinedPositions.add(newNode.getPosition());
  }

  /** Returns whether the given nodes are diagonal to each other. */
  private boolean isDiagonalMove(Node from, Node to) {
    int xDifference = Math.abs(from.getPosition().getBlockX() - to.getPosition().getBlockX());
    int zDifference = Math.abs(from.getPosition().getBlockZ() - to.getPosition().getBlockZ());

    return xDifference != 0 && zDifference != 0;
  }

  /**
   * Returns whether the diagonal jump is possible by checking if the adjacent nodes are passable or
   * not. With adjacent nodes are the shared overlapping neighbours meant.
   */
  private boolean isReachable(Node from, Node to, PathfinderStrategy strategy) {
    boolean hasYDifference = from.getPosition().getBlockY() != to.getPosition().getBlockY();
    PathVector[] offsets = Offset.VERTICAL_AND_HORIZONTAL.getVectors();

    for (PathVector vector1 : offsets) {
      if (vector1.getY() != 0) continue;

      Node neighbour1 = createNeighbourNode(from, vector1);
      for (PathVector vector2 : offsets) {
        if (vector2.getY() != 0) continue;

        Node neighbour2 = createNeighbourNode(to, vector2);
        if (neighbour1.getPosition().equals(neighbour2.getPosition())) {
          /*
           * if it has a Y difference, we also need to check the nodes above or below,
           *  depending on the Y difference
           */
          boolean heightDifferencePassable =
              isHeightDifferencePassable(from, to, vector1, hasYDifference);
          if (strategy.isValid(
                  new PathValidationContext(
                      neighbour1.getPosition(),
                      neighbour1.getParent().getPosition(),
                      snapshotManager))
              && heightDifferencePassable) return true;
        }
      }
    }

    return false;
  }

  /**
   * Return whether the height difference between the given nodes is passable. If the nodes have no
   * height difference, this will always return true.
   */
  private boolean isHeightDifferencePassable(
      Node from, Node to, PathVector vector1, boolean hasYDifference) {
    if (!hasYDifference) return true;

    int yDifference = from.getPosition().getBlockY() - to.getPosition().getBlockY();
    Node neighbour3 = createNeighbourNode(from, vector1.add(new PathVector(0, yDifference, 0)));

    return snapshotManager.getBlock(neighbour3.getPosition()).isPassable();
  }

  /**
   * Determines whether the given position is within the bounds of the world.
   *
   * @param position the position to check
   * @return {@code true} if the position is within the bounds of the world, {@code false} otherwise
   */
  private boolean isWithinWorldBounds(PathPosition position) {
    return position.getPathEnvironment().getMinHeight() < position.getBlockY()
        && position.getBlockY() < position.getPathEnvironment().getMaxHeight();
  }

  /**
   * Fetches the neighbours of the given node.
   *
   * @param currentNode the node to fetch neighbours for
   * @param allowingDiagonal
   * @return a collection of neighbour nodes
   */
  private Collection<Node> fetchValidNeighbours(
      Collection<Node> nodeQueue,
      Set<PathPosition> examinedPositions,
      Node currentNode,
      PathfinderStrategy strategy,
      boolean allowingDiagonal) {
    Set<Node> newNodes = new HashSet<>(offset.getVectors().length);

    for (PathVector vector : offset.getVectors()) {
      Node newNode = createNeighbourNode(currentNode, vector);

      if (isNodeValid(
          currentNode, newNode, nodeQueue, examinedPositions, strategy, allowingDiagonal)) {
        newNodes.add(newNode);
      }
    }

    return newNodes;
  }

  /**
   * Fetches the path represented by the given node by retracing the steps from the node's parent.
   *
   * @param node the node to fetch the path for
   * @return the path represented by the given node
   */
  private Path fetchRetracedPath(@NonNull Node node) {

    if (node.getParent() == null)
      return new PathImpl(
          node.getStart(), node.getTarget(), Collections.singletonList(node.getPosition()));

    List<PathPosition> path = tracePathFromNode(node);
    return new PathImpl(node.getStart(), node.getTarget(), path);
  }

  /** Creates a new node based on the given node and offset. */
  private Node createNeighbourNode(Node currentNode, PathVector offset) {
    Node newNode =
        new Node(
            currentNode.getPosition().add(offset),
            currentNode.getStart(),
            currentNode.getTarget(),
            currentNode.getDepth() + 1);
    newNode.setParent(currentNode);
    return newNode;
  }

  /**
   * @return whether the given node is invalid or not
   */
  private boolean isNodeInvalid(
      Node node,
      Collection<Node> nodeQueue,
      Set<PathPosition> examinedPositions,
      PathfinderStrategy strategy) {
    return examinedPositions.contains(node.getPosition())
        || nodeQueue.contains(node)
        || !isWithinWorldBounds(node.getPosition())
        || !strategy.isValid(
            new PathValidationContext(
                node.getPosition(), node.getParent().getPosition(), snapshotManager));
  }

  /** Traces the path from the given node by retracing the steps from the node's parent. */
  private List<PathPosition> tracePathFromNode(Node endNode) {
    List<PathPosition> path = new ArrayList<>();
    Node currentNode = endNode;

    while (currentNode != null) {
      path.add(currentNode.getPosition());
      currentNode = currentNode.getParent();
    }

    Collections.reverse(path); // make it the right order
    return path;
  }
}
