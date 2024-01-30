package org.patheloper.model.pathing.pathfinder;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import lombok.NonNull;
import org.bukkit.event.Cancellable;
import org.patheloper.BStatsHandler;
import org.patheloper.Pathetic;
import org.patheloper.api.event.PathingFinishedEvent;
import org.patheloper.api.event.PathingStartFindEvent;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.terrain.TerrainProvider;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.bukkit.event.EventPublisher;
import org.patheloper.model.pathing.Offset;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.util.ErrorLogger;

abstract class AbstractPathfinder implements Pathfinder {

  protected static final Set<PathPosition> EMPTY_LINKED_HASHSET =
      Collections.unmodifiableSet(new LinkedHashSet<>(0));

  private static final Executor PATHING_EXECUTOR =
      new ThreadPoolExecutor(
          Runtime.getRuntime().availableProcessors() / 4,
          Runtime.getRuntime().availableProcessors(),
          250L,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(10000),
          new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Pathfinder Task-%d").build(),
          new ThreadPoolExecutor.AbortPolicy());

  protected final PathingRuleSet pathingRuleSet;

  protected final Offset offset;
  protected final TerrainProvider terrainProvider;

  protected AbstractPathfinder(PathingRuleSet pathingRuleSet, TerrainProvider terrainProvider) {
    this.pathingRuleSet = pathingRuleSet;

    this.offset = pathingRuleSet.isAllowingDiagonal() ? Offset.MERGED : Offset.VERTICAL_AND_HORIZONTAL;
    this.terrainProvider = terrainProvider;
  }

  @Override
  public @NonNull CompletionStage<PathfinderResult> findPath(
      @NonNull PathPosition start,
      @NonNull PathPosition target,
      @NonNull PathfinderStrategy strategy) {
    PathingStartFindEvent startEvent = raiseStart(start, target, strategy);

    if (initialChecksFailed(start, target, startEvent))
      return CompletableFuture.completedFuture(
          finishPathing(
              new PathfinderResultImpl(
                  PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET))));

    return producePathing(start, target, strategy);
  }

  protected PathfinderResult finishPathing(PathfinderResult pathfinderResult) {
    EventPublisher.raiseEvent(new PathingFinishedEvent(pathfinderResult));
    return pathfinderResult;
  }

  protected TerrainProvider getSnapshotManager() {
    return terrainProvider;
  }

  private PathingStartFindEvent raiseStart(
      PathPosition start, PathPosition target, PathfinderStrategy strategy) {
    PathingStartFindEvent startEvent = new PathingStartFindEvent(start, target, strategy);
    EventPublisher.raiseEvent(startEvent);

    return startEvent;
  }

  private boolean initialChecksFailed(
      PathPosition start, PathPosition target, Cancellable startEvent) {

    if (startEvent.isCancelled()
        || !start.getPathEnvironment().equals(target.getPathEnvironment())
        || start.isInSameBlock(target)) return true;

    return this.pathingRuleSet.isAllowingFailFast() && isBlockUnreachable(target)
        || isBlockUnreachable(start);
  }

  private boolean isBlockUnreachable(PathPosition position) {
    for (PathVector vector : offset.getVectors()) {

      PathPosition offsetPosition = position.add(vector);
      PathBlock pathBlock = this.getSnapshotManager().getBlock(offsetPosition);

      if (pathBlock == null) continue;

      if (pathBlock.isPassable()) return false;
    }

    return true;
  }

  private CompletionStage<PathfinderResult> producePathing(
      PathPosition start, PathPosition target, PathfinderStrategy strategy) {
    BStatsHandler.increasePathCount();

    CompletionStage<PathfinderResult> result;
    if (pathingRuleSet.isAsync()) result = produceAsyncPathing(start, target, strategy);
    else result = produceSyncPathing(start, target, strategy);

    strategy.cleanup();
    return result;
  }

  private CompletionStage<PathfinderResult> produceAsyncPathing(
      PathPosition start, PathPosition target, PathfinderStrategy strategy) {
    return CompletableFuture.supplyAsync(
            () -> {
              try {
                return resolvePath(start, target, strategy);
              } catch (Exception e) {
                throw ErrorLogger.logFatalError("Failed to find path async", e);
              }
            },
            PATHING_EXECUTOR)
        .thenApply(this::finishPathing)
        .exceptionally(this::exceptionHandler);
  }

  private CompletionStage<PathfinderResult> produceSyncPathing(
      PathPosition start, PathPosition target, PathfinderStrategy strategy) {
    try {
      return CompletableFuture.completedFuture(resolvePath(start, target, strategy));
    } catch (Exception e) {
      throw ErrorLogger.logFatalError("Failed to find path sync", e);
    }
  }

  private PathfinderResult exceptionHandler(Throwable throwable) {
    Logger logger = Pathetic.getPluginInstance().getLogger();

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    throwable.printStackTrace(pw);
    logger.severe(sw.toString());

    return finishPathing(
        new PathfinderResultImpl(PathState.FAILED, new PathImpl(null, null, EMPTY_LINKED_HASHSET)));
  }

  // name clash with the interface, therefore "resolve" instead of "find"
  protected abstract PathfinderResult resolvePath(
      PathPosition start, PathPosition target, PathfinderStrategy strategy);
}
