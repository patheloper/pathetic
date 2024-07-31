package org.patheloper.model.pathing.pathfinder;

import java.util.*;
import lombok.NonNull;
import org.jheaps.tree.FibonacciHeap;
import org.patheloper.api.pathing.configuration.PathfinderConfiguration;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.filter.PathValidationContext;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.model.pathing.Node;
import org.patheloper.model.pathing.Offset;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.util.ExpiringHashMap;
import org.patheloper.util.FilterDependencyValidator;
import org.patheloper.util.GridRegionData;
import org.patheloper.util.Tuple3;
import org.patheloper.util.WatchdogUtil;

public class AStarPathfinder extends AbstractPathfinder {

  private static final int DEFAULT_GRID_CELL_SIZE = 12;

  /**
   * The grid map used to store the regional examined positions and Bloom filters for each grid
   * region.
   */
  private final Map<Tuple3<Integer>, ExpiringHashMap.Entry<GridRegionData>> gridMap =
      new ExpiringHashMap<>();

  public AStarPathfinder(PathfinderConfiguration pathfinderConfiguration) {
    super(pathfinderConfiguration);
  }

  @Override
  protected PathfinderResult resolvePath(
      PathPosition start, PathPosition target, List<PathFilter> filters) {
    Node startNode = createStartNode(start, target);
    FibonacciHeap<Double, Node> nodeQueue = new FibonacciHeap<>();
    nodeQueue.insert(startNode.getFCost(), startNode);

    Set<PathPosition> examinedPositions = new HashSet<>();
    int depth = 1;
    Node fallbackNode = startNode;

    while (!nodeQueue.isEmpty() && depth <= pathfinderConfiguration.getMaxIterations()) {
      tickWatchdogIfNeeded(depth);
      Node currentNode = nodeQueue.deleteMin().getValue();
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
          filters,
          this.pathfinderConfiguration.isAllowingDiagonal());
      depth++;
    }

    return backupPathfindingOrFailure(depth, start, target, filters, fallbackNode);
  }

  private Node createStartNode(PathPosition start, PathPosition target) {
    return new Node(
        start.floor(),
        start.floor(),
        target.floor(),
        pathfinderConfiguration.getHeuristicWeights(),
        0);
  }

  private void tickWatchdogIfNeeded(int depth) {
    if (depth % 500 == 0) {
      WatchdogUtil.tickWatchdog();
    }
  }

  private boolean hasReachedLengthLimit(Node currentNode) {
    return pathfinderConfiguration.getMaxLength() != 0
        && currentNode.getDepth() > pathfinderConfiguration.getMaxLength();
  }

  private PathfinderResult finishPathing(PathState pathState, Node currentNode) {
    return finishPathing(new PathfinderResultImpl(pathState, fetchRetracedPath(currentNode)));
  }

  /** If the pathfinder has failed to find a path, it will try to still give a result. */
  private PathfinderResult backupPathfindingOrFailure(
      int depth,
      PathPosition start,
      PathPosition target,
      List<PathFilter> filters,
      Node fallbackNode) {

    Optional<PathfinderResult> maxIterationsResult = maxIterationsReached(depth, fallbackNode);
    if (maxIterationsResult.isPresent()) {
      return maxIterationsResult.get();
    }

    Optional<PathfinderResult> counterCheckResult = counterCheck(start, target, filters);
    if (counterCheckResult.isPresent()) {
      return counterCheckResult.get();
    }

    Optional<PathfinderResult> fallbackResult = fallback(fallbackNode);
    return fallbackResult.orElseGet(
        () ->
            finishPathing(
                new PathfinderResultImpl(
                    PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET))));
  }

  private Optional<PathfinderResult> maxIterationsReached(int depth, Node fallbackNode) {
    if (depth > pathfinderConfiguration.getMaxIterations())
      return Optional.of(
          finishPathing(
              new PathfinderResultImpl(
                  PathState.MAX_ITERATIONS_REACHED, fetchRetracedPath(fallbackNode))));
    return Optional.empty();
  }

  private Optional<PathfinderResult> fallback(Node fallbackNode) {
    if (pathfinderConfiguration.isAllowingFallback())
      return Optional.of(
          finishPathing(
              new PathfinderResultImpl(PathState.FALLBACK, fetchRetracedPath(fallbackNode))));
    return Optional.empty();
  }

  private Optional<PathfinderResult> counterCheck(
      PathPosition start, PathPosition target, List<PathFilter> filters) {
    if (!pathfinderConfiguration.isCounterCheck()) {
      return Optional.empty();
    }

    AStarPathfinder aStarPathfinder =
        new AStarPathfinder(
            PathfinderConfiguration.deepCopy(pathfinderConfiguration).withCounterCheck(false));
    PathfinderResult pathfinderResult = aStarPathfinder.resolvePath(target, start, filters);

    if (pathfinderResult.getPathState() == PathState.FOUND) {
      return Optional.of(pathfinderResult);
    }

    return Optional.empty();
  }

  private void evaluateNewNodes(
      FibonacciHeap<Double, Node> nodeQueue,
      Set<PathPosition> examinedPositions,
      Node currentNode,
      List<PathFilter> filters,
      boolean allowingDiagonal) {
    Collection<Node> newNodes =
        fetchValidNeighbours(examinedPositions, currentNode, filters, allowingDiagonal);
    for (Node newNode : newNodes) {
      nodeQueue.insert(newNode.getHeuristic().get(), newNode);
    }
  }

  private boolean isNodeValid(
      Node currentNode,
      Node newNode,
      Set<PathPosition> examinedPositions,
      List<PathFilter> filters,
      boolean allowingDiagonal) {
    if (isNodeInvalid(newNode, filters)) return false;

    if (!allowingDiagonal) return examinedPositions.add(newNode.getPosition());

    if (!isDiagonalMove(currentNode, newNode)) return examinedPositions.add(newNode.getPosition());

    return isReachable(currentNode, newNode, filters)
        && examinedPositions.add(newNode.getPosition());
  }

  private boolean isDiagonalMove(Node from, Node to) {
    int xDifference = Math.abs(from.getPosition().getBlockX() - to.getPosition().getBlockX());
    int zDifference = Math.abs(from.getPosition().getBlockZ() - to.getPosition().getBlockZ());

    return xDifference != 0 && zDifference != 0;
  }

  /**
   * Returns whether the diagonal jump is possible by checking if the adjacent nodes are passable or
   * not. With adjacent nodes are the shared overlapping neighbours meant.
   */
  private boolean isReachable(Node from, Node to, List<PathFilter> filters) {
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

          if (doesAnyFilterPass(filters, neighbour1) && heightDifferencePassable) return true;
        }
      }
    }

    return false;
  }

  private boolean isHeightDifferencePassable(
      Node from, Node to, PathVector vector1, boolean hasHeightDifference) {
    if (!hasHeightDifference) return true;

    int yDifference = from.getPosition().getBlockY() - to.getPosition().getBlockY();
    Node neighbour3 = createNeighbourNode(from, vector1.add(new PathVector(0, yDifference, 0)));

    return snapshotManager.getBlock(neighbour3.getPosition()).isPassable();
  }

  private Collection<Node> fetchValidNeighbours(
      Set<PathPosition> examinedPositions,
      Node currentNode,
      List<PathFilter> filters,
      boolean allowingDiagonal) {
    Set<Node> newNodes = new HashSet<>(offset.getVectors().length);

    for (PathVector vector : offset.getVectors()) {
      Node newNode = createNeighbourNode(currentNode, vector);

      if (isNodeValid(currentNode, newNode, examinedPositions, filters, allowingDiagonal)) {
        newNodes.add(newNode);
      }
    }

    return newNodes;
  }

  private Path fetchRetracedPath(@NonNull Node node) {
    if (node.getParent() == null)
      return new PathImpl(
          node.getStart(), node.getTarget(), Collections.singletonList(node.getPosition()));

    List<PathPosition> path = tracePathFromNode(node);
    return new PathImpl(node.getStart(), node.getTarget(), path);
  }

  private Node createNeighbourNode(Node currentNode, PathVector offset) {
    Node newNode =
        new Node(
            currentNode.getPosition().add(offset),
            currentNode.getStart(),
            currentNode.getTarget(),
            pathfinderConfiguration.getHeuristicWeights(),
            currentNode.getDepth() + 1);
    newNode.setParent(currentNode);
    return newNode;
  }

  /**
   * Checks if the node is invalid. A node is invalid if it is outside the world bounds or is not
   * valid according to the filters.
   */
  private boolean isNodeInvalid(Node node, List<PathFilter> filters) {
    int gridX = node.getPosition().getBlockX() / DEFAULT_GRID_CELL_SIZE;
    int gridY = node.getPosition().getBlockY() / DEFAULT_GRID_CELL_SIZE;
    int gridZ = node.getPosition().getBlockZ() / DEFAULT_GRID_CELL_SIZE;

    GridRegionData regionData =
        gridMap
            .computeIfAbsent(
                new Tuple3<>(gridX, gridY, gridZ),
                k -> new ExpiringHashMap.Entry<>(new GridRegionData()))
            .getValue();

    regionData.getRegionalExaminedPositions().add(node.getPosition());

    if (regionData.getBloomFilter().mightContain(node.getPosition())) {
      if (regionData.getRegionalExaminedPositions().contains(node.getPosition())) {
        return true;
      }
    }

    return !isWithinWorldBounds(node.getPosition()) || !doesAnyFilterPass(filters, node);
  }

  private boolean doesAnyFilterPass(List<PathFilter> filters, Node node) {
    Map<Class<? extends PathFilter>, Boolean> cache = new HashMap<>();

    for (PathFilter filter : filters) {
      PathValidationContext context =
          new PathValidationContext(
              node.getPosition(),
              node.getParent() != null ? node.getParent().getPosition() : null,
              snapshotManager);

      if (!FilterDependencyValidator.validateDependencies(filter, context, filters, cache)) {
        continue;
      }

      if (cache.computeIfAbsent(filter.getClass(), k -> filter.filter(context))) {
        return true;
      }
    }
    return false;
  }

  private boolean isWithinWorldBounds(PathPosition position) {
    return position.getPathEnvironment().getMinHeight() < position.getBlockY()
        && position.getBlockY() < position.getPathEnvironment().getMaxHeight();
  }

  private List<PathPosition> tracePathFromNode(Node endNode) {
    List<PathPosition> path = new ArrayList<>();
    Node currentNode = endNode;

    while (currentNode != null) {
      path.add(currentNode.getPosition());
      currentNode = currentNode.getParent();
    }

    Collections.reverse(path); // Reverse the path to get the correct order
    return path;
  }
}
