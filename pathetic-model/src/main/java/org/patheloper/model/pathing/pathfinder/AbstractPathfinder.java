package org.patheloper.model.pathing.pathfinder;

import lombok.NonNull;
import org.patheloper.BStatsHandler;
import org.patheloper.Pathetic;
import org.patheloper.api.event.EventPublisher;
import org.patheloper.api.event.PathingFinishedEvent;
import org.patheloper.api.event.PathingStartFindEvent;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.configuration.PathfinderConfiguration;
import org.patheloper.api.pathing.filter.PathFilter;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.model.pathing.Offset;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.model.snapshot.FailingSnapshotManager;
import org.patheloper.util.ErrorLogger;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The AbstractPathfinder class provides a skeletal implementation of the Pathfinder interface and defines the common
 * behavior for all pathfinding algorithms. It provides a default implementation for determining the offset and snapshot
 * manager based on the pathing rule set.
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
      ? initiateAsyncPathing(start, target, filters)
      : initiateSyncPathing(start, target, filters);
  }

  private CompletionStage<PathfinderResult> initiateAsyncPathing(
    PathPosition start, PathPosition target, List<PathFilter> filters) {










    return CompletableFuture.supplyAsync(
        () -> {
          try {
            return resolvePath(start, target, filters);
          } catch (Exception e) {
            throw ErrorLogger.logFatalErrorWithStacktrace("Failed to find path async", e);
          }
        },
        PATHING_EXECUTOR)
      .thenApply(this::finishPathing)
      .exceptionally(throwable -> handleException(start, target));
  }

  private CompletionStage<PathfinderResult> initiateSyncPathing(
    PathPosition start, PathPosition target, List<PathFilter> filters) {










    try {
      return CompletableFuture.completedFuture(resolvePath(start, target, filters));
    } catch (Exception e) {
      throw ErrorLogger.logFatalError("Failed to find path sync", e);
    }
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

  protected abstract PathfinderResult resolvePath(
    PathPosition start, PathPosition target, List<PathFilter> filters);
}
