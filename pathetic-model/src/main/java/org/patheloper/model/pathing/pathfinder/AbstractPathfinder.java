package org.patheloper.model.pathing.pathfinder;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import lombok.NonNull;
import org.jheaps.tree.FibonacciHeap;
import org.patheloper.BStatsHandler;
import org.patheloper.Pathetic;
import org.patheloper.api.event.EventPublisher;
import org.patheloper.api.event.PathingFinishedEvent;
import org.patheloper.api.event.PathingStartFindEvent;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.configuration.PathfinderConfiguration;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.model.pathing.Node;
import org.patheloper.model.pathing.Offset;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.model.snapshot.FailingSnapshotManager;
import org.patheloper.util.ErrorLogger;

/**
 * The AbstractPathfinder class provides a skeletal implementation of the Pathfinder interface and
 * defines the common behavior for all pathfinding algorithms. It provides a default implementation
 * for determining the offset and snapshot manager based on the pathing rule set.
 *
 * <p>This class now operates in a tick-wise manner, meaning that the pathfinding process progresses
 * incrementally, with each "tick" representing a small step in the algorithm's execution. At each
 * tick, the algorithm evaluates nodes, updates the priority queue, and checks for conditions such
 * as reaching the target or encountering an abort signal.
 */
abstract class AbstractPathfinder implements Pathfinder {

  protected static final Set<PathPosition> EMPTY_LINKED_HASHSET =
      Collections.unmodifiableSet(new LinkedHashSet<>(0));

  private static final SnapshotManager SIMPLE_SNAPSHOT_MANAGER = new FailingSnapshotManager();
  private static final SnapshotManager LOADING_SNAPSHOT_MANAGER =
      new FailingSnapshotManager.RequestingSnapshotManager();

  private static final ExecutorService PATHING_EXECUTOR = Executors.newWorkStealingPool();

  static {
    Pathetic.addShutdownListener(PATHING_EXECUTOR::shutdown);
  }

  protected final PathfinderConfiguration pathfinderConfiguration;
  protected final Offset offset;
  protected final SnapshotManager snapshotManager;

  private volatile boolean aborted;

  protected AbstractPathfinder(PathfinderConfiguration pathfinderConfiguration) {
    this.pathfinderConfiguration = pathfinderConfiguration;
    this.offset = determineOffset(pathfinderConfiguration);
    this.snapshotManager = determineSnapshotManager(pathfinderConfiguration);
  }

  private Offset determineOffset(PathfinderConfiguration pathfinderConfiguration) {
    return pathfinderConfiguration.isAllowingDiagonal()
        ? Offset.MERGED
        : Offset.VERTICAL_AND_HORIZONTAL;
  }

  private SnapshotManager determineSnapshotManager(
      PathfinderConfiguration pathfinderConfiguration) {
    return pathfinderConfiguration.isLoadingChunks()
        ? LOADING_SNAPSHOT_MANAGER
        : SIMPLE_SNAPSHOT_MANAGER;
  }

  @Override
  public @NonNull CompletionStage<PathfinderResult> findPath(
      @NonNull PathPosition start,
      @NonNull PathPosition target,
      @Nullable List<PathFilter> filters) {

    if (filters == null) filters = Collections.emptyList();

    raiseStartEvent(start, target, filters);

    if (shouldSkipPathing(start, target)) {
      return CompletableFuture.completedFuture(
          finishPathing(
              new PathfinderResultImpl(
                  PathState.INITIALLY_FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET))));
    }

    return initiatePathing(start, target, filters);
  }

  /** Give the pathfinder the final shot */
  @Override
  public void abort() {
    this.aborted = true;
  }

  private void cleanupFilters(List<PathFilter> filters) {
    filters.forEach(PathFilter::cleanup);
  }

  private boolean shouldSkipPathing(PathPosition start, PathPosition target) {
    return !isSameEnvironment(start, target)
        || isSameBlock(start, target)
        || isFastFailEnabledAndBlockUnreachable(start, target);
  }

  private boolean isSameEnvironment(PathPosition start, PathPosition target) {
    return start.getPathEnvironment().equals(target.getPathEnvironment());
  }

  private boolean isSameBlock(PathPosition start, PathPosition target) {
    return start.isInSameBlock(target);
  }

  private boolean isFastFailEnabledAndBlockUnreachable(PathPosition start, PathPosition target) {
    return this.pathfinderConfiguration.isAllowingFailFast()
        && (isBlockUnreachable(target) || isBlockUnreachable(start));
  }

  private boolean isBlockUnreachable(PathPosition position) {
    for (PathVector vector : offset.getVectors()) {
      PathPosition offsetPosition = position.add(vector);
      PathBlock pathBlock = this.snapshotManager.getBlock(offsetPosition);
      if (pathBlock != null && pathBlock.isPassable()) {
        return false;
      }
    }
    return true;
  }

  private CompletionStage<PathfinderResult> initiatePathing(
      PathPosition start, PathPosition target, List<PathFilter> filters) {
    BStatsHandler.increasePathCount();
    return pathfinderConfiguration.isAsync()
        ? CompletableFuture.supplyAsync(
                () -> executePathingAndCallCleanup(start, target, filters), PATHING_EXECUTOR)
            .thenApply(this::finishPathing)
            .exceptionally(throwable -> handleException(start, target))
        : initiateSyncPathing(start, target, filters);
  }

  private PathfinderResult executePathing(
      PathPosition start, PathPosition target, List<PathFilter> filters) {
    try {
      Node startNode = createStartNode(start, target);
      FibonacciHeap<Double, Node> nodeQueue = new FibonacciHeap<>();
      nodeQueue.insert(startNode.getFCost(), startNode);

      Set<PathPosition> examinedPositions = new HashSet<>();
      Depth depth = new Depth(1);
      Node fallbackNode = startNode;

      while (!nodeQueue.isEmpty()
          && depth.getDepth() <= pathfinderConfiguration.getMaxIterations()) {

        if (isAborted()) {
          return resetAbortedAndFinishPathing(PathState.ABORTED, fallbackNode);
        }

        Node currentNode = nodeQueue.deleteMin().getValue();
        fallbackNode = currentNode;

        if (hasReachedLengthLimit(currentNode)) {
          return resetAbortedAndFinishPathing(PathState.LENGTH_LIMITED, currentNode);
        }

        if (currentNode.isTarget()) {
          return resetAbortedAndFinishPathing(PathState.FOUND, currentNode);
        }

        tick(start, target, currentNode, depth, nodeQueue, examinedPositions, filters);
      }

      Optional<PathfinderResult> maxIterationsResult = maxIterationsReached(depth, fallbackNode);
      if (maxIterationsResult.isPresent()) {
        return maxIterationsResult.get();
      }

      return backupPathfindingOrFailure(start, target, filters, fallbackNode);
    } catch (Exception e) {
      throw ErrorLogger.logFatalErrorWithStacktrace("Failed to find path", e);
    }
  }

  private boolean isAborted() {
    return aborted;
  }

  private CompletionStage<PathfinderResult> initiateSyncPathing(
      PathPosition start, PathPosition target, List<PathFilter> filters) {
    try {
      return CompletableFuture.completedFuture(
          executePathingAndCallCleanup(start, target, filters));
    } catch (Exception e) {
      throw ErrorLogger.logFatalError("Failed to find path sync", e);
    }
  }

  private PathfinderResult executePathingAndCallCleanup(
      PathPosition start, PathPosition target, List<PathFilter> filters) {
    PathfinderResult result = executePathing(start, target, filters);
    cleanupFilters(filters);
    return result;
  }

  private PathfinderResult handleException(PathPosition start, PathPosition target) {
    return finishPathing(
        new PathfinderResultImpl(
            PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));
  }

  protected PathfinderResult finishPathing(PathfinderResult pathfinderResult) {
    raiseFinishedEvent(pathfinderResult);
    return pathfinderResult;
  }

  private void raiseFinishedEvent(PathfinderResult pathfinderResult) {
    PathingFinishedEvent finishedEvent = new PathingFinishedEvent(pathfinderResult);
    EventPublisher.raiseEvent(finishedEvent);
  }

  private void raiseStartEvent(PathPosition start, PathPosition target, List<PathFilter> filters) {
    PathingStartFindEvent startEvent = new PathingStartFindEvent(start, target, filters);
    EventPublisher.raiseEvent(startEvent);
  }

  private Node createStartNode(PathPosition start, PathPosition target) {
    return new Node(
        start.floor(),
        start.floor(),
        target.floor(),
        pathfinderConfiguration.getHeuristicWeights(),
        0);
  }

  private boolean hasReachedLengthLimit(Node currentNode) {
    return pathfinderConfiguration.getMaxLength() != 0
        && currentNode.getDepth() > pathfinderConfiguration.getMaxLength();
  }

  private PathfinderResult resetAbortedAndFinishPathing(PathState pathState, Node currentNode) {
    aborted = false;
    return finishPathing(new PathfinderResultImpl(pathState, fetchRetracedPath(currentNode)));
  }

  /** If the pathfinder has failed to find a path, it will try to still give a result. */
  private PathfinderResult backupPathfindingOrFailure(
      PathPosition start, PathPosition target, List<PathFilter> filters, Node fallbackNode) {

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

  private Optional<PathfinderResult> maxIterationsReached(Depth depth, Node fallbackNode) {
    if (depth.getDepth() > pathfinderConfiguration.getMaxIterations())
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

    PathfinderConfiguration pathConfig =
        PathfinderConfiguration.deepCopy(pathfinderConfiguration).withCounterCheck(false);
    try {
      Pathfinder pathfinder =
          getClass().getConstructor(PathfinderConfiguration.class).newInstance(pathConfig);
      PathfinderResult pathfinderResult =
          pathfinder.findPath(start, target, filters).toCompletableFuture().get();

      if (pathfinderResult.getPathState() == PathState.FOUND) {
        return Optional.of(pathfinderResult);
      }
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException
        | ExecutionException
        | InterruptedException e) {
      throw new RuntimeException(e);
    }

    return Optional.empty();
  }

  private Path fetchRetracedPath(@NonNull Node node) {
    if (node.getParent() == null)
      return new PathImpl(
          node.getStart(), node.getTarget(), Collections.singletonList(node.getPosition()));

    List<PathPosition> path = tracePathFromNode(node);
    return new PathImpl(node.getStart(), node.getTarget(), path);
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

  /** The tick method is called to tick the pathfinding algorithm. */
  protected abstract void tick(
      PathPosition start,
      PathPosition target,
      Node currentNode,
      Depth depth,
      FibonacciHeap<Double, Node> nodeQueue,
      Set<PathPosition> examinedPositions,
      List<PathFilter> filters);
}
