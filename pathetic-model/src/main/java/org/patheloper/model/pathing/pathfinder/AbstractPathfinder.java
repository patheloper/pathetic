package org.patheloper.model.pathing.pathfinder;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.NonNull;
import org.bukkit.event.Cancellable;
import org.patheloper.api.event.PathingFinishedEvent;
import org.patheloper.api.event.PathingStartFindEvent;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.bukkit.event.EventPublisher;
import org.patheloper.model.pathing.Offset;
import org.patheloper.model.pathing.algorithm.PostOptimizationAlgorithm;
import org.patheloper.model.pathing.handler.PathfinderAsyncExceptionHandler;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.model.snapshot.FailingSnapshotManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

abstract class AbstractPathfinder implements Pathfinder {

    protected static final Set<PathPosition> EMPTY_LINKED_HASHSET = Collections.unmodifiableSet(new LinkedHashSet<>(0));

    private static final SnapshotManager SIMPLE_SNAPSHOT_MANAGER = new FailingSnapshotManager();
    private static final SnapshotManager LOADING_SNAPSHOT_MANAGER = new FailingSnapshotManager.RequestingSnapshotManager();

    private static final Executor PATHING_EXECUTOR =
            new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors() / 4,
                    Runtime.getRuntime().availableProcessors(),
                    250L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(10000),
                    new ThreadFactoryBuilder()
                            .setUncaughtExceptionHandler(new PathfinderAsyncExceptionHandler())
                            .setDaemon(true)
                            .setNameFormat("Pathfinder Task-%d")
                            .build(),
                    new ThreadPoolExecutor.AbortPolicy());

    protected final PathingRuleSet pathingRuleSet;
    protected final Offset offset;
    protected final SnapshotManager snapshotManager;

    protected AbstractPathfinder(PathingRuleSet pathingRuleSet) {

        this.pathingRuleSet = pathingRuleSet;

        this.offset = pathingRuleSet.isAllowingDiagonal() ? Offset.MERGED : Offset.VERTICAL_AND_HORIZONTAL;
        this.snapshotManager = pathingRuleSet.isLoadingChunks() ? LOADING_SNAPSHOT_MANAGER : SIMPLE_SNAPSHOT_MANAGER;
    }

    @Override
    public @NonNull CompletionStage<PathfinderResult> findPath(@NonNull PathPosition start, @NonNull PathPosition target) {

        PathfinderStrategy strategy = instantiateStrategy();
        PathingStartFindEvent startEvent = raiseStart(start, target, strategy);

        if(initialChecksFailed(start, target, startEvent))
            return CompletableFuture.completedFuture(finishPathing(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET))));

        return producePathing(start, target, strategy);
    }

    protected PathfinderResult finishPathing(PathfinderResult pathfinderResult) {
        EventPublisher.raiseEvent(new PathingFinishedEvent(pathfinderResult));
        return pathfinderResult;
    }

    protected SnapshotManager getSnapshotManager() {
        return snapshotManager;
    }

    private PathingStartFindEvent raiseStart(PathPosition start, PathPosition target, PathfinderStrategy strategy) {

        PathingStartFindEvent startEvent = new PathingStartFindEvent(start, target, strategy);
        EventPublisher.raiseEvent(startEvent);

        return startEvent;
    }

    private boolean initialChecksFailed(PathPosition start, PathPosition target, Cancellable startEvent) {

        // Do some initial checks to make sure that we should even bother with pathfinding
        if (startEvent.isCancelled())
            return true;

        if (!start.getPathEnvironment().equals(target.getPathEnvironment()))
            return true;

        if (start.isInSameBlock(target))
            return true;

        return this.pathingRuleSet.isAllowingFailFast() && isBlockUnreachable(target) || isBlockUnreachable(start);
    }

    private boolean isBlockUnreachable(PathPosition position) {

        for(PathVector vector : offset.getVectors()) {

            PathPosition offsetPosition = position.add(vector);
            PathBlock pathBlock = this.getSnapshotManager().getBlock(offsetPosition);

            if(pathBlock.isPassable())
                return false;
        }

        return true;
    }

    private PathfinderStrategy instantiateStrategy() {
        try {
            return pathingRuleSet.getStrategy().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException("Failed to instantiate PathfinderStrategy", e);
        }
    }

    /*
     * Bloating up like a bubble until a reachable block is found
     * The block itself might not be passable, but at least reachable from the outside
     *
     * NOTE: The reachable block is not guaranteed to be the closest reachable block
     */
    private PathBlock bubbleSearchAlternative(PathPosition target, Offset offset, SnapshotManager snapshotManager) {

        Set<PathPosition> newPositions = new HashSet<>();
        newPositions.add(target);

        Set<PathPosition> examinedPositions = new HashSet<>();
        while (!newPositions.isEmpty()) {

            Set<PathPosition> nextPositions = new HashSet<>();
            for (PathPosition position : newPositions) {

                for (PathVector vector : offset.getVectors()) {

                    PathPosition offsetPosition = position.add(vector);
                    PathBlock pathBlock = snapshotManager.getBlock(offsetPosition);

                    if (pathBlock.isPassable() && !pathBlock.getPathPosition().isInSameBlock(target))
                        return pathBlock;

                    if (!examinedPositions.contains(offsetPosition))
                        nextPositions.add(offsetPosition);
                }

                examinedPositions.add(position);
            }

            newPositions = nextPositions;
        }

        return snapshotManager.getBlock(target);
    }

    private PathPosition relocateTargetPosition(PathPosition target) {
        if (pathingRuleSet.isAllowingAlternateTarget() && isBlockUnreachable(target))
            return bubbleSearchAlternative(target, offset, snapshotManager).getPathPosition();
        return target;
    }

    private CompletionStage<PathfinderResult> producePathing(PathPosition start, PathPosition target, PathfinderStrategy strategy) {
        if(pathingRuleSet.isAsync())
            return produceAsyncPathing(start, target, strategy);
        return produceSyncPathing(start, target, strategy);
    }
    
    private CompletionStage<PathfinderResult> produceAsyncPathing(PathPosition start, PathPosition target, PathfinderStrategy strategy) {
        return CompletableFuture.supplyAsync(() -> {
            PathfinderResult result = findPath(start, relocateTargetPosition(target), strategy);
            if(pathingRuleSet.isPostOptimization())
                return finishPathing(new PathfinderResultImpl(result.getPathState(), new PostOptimizationAlgorithm(pathingRuleSet).apply(result.getPath())));
            return finishPathing(result);
        }, PATHING_EXECUTOR);
    }
    
    private CompletionStage<PathfinderResult> produceSyncPathing(PathPosition start, PathPosition target, PathfinderStrategy strategy) {
        PathfinderResult pathfinderResult = findPath(start, relocateTargetPosition(target), strategy);
        if(pathingRuleSet.isPostOptimization())
            return CompletableFuture.completedFuture(finishPathing(new PathfinderResultImpl(pathfinderResult.getPathState(), new PostOptimizationAlgorithm(pathingRuleSet).apply(pathfinderResult.getPath()))));
        return CompletableFuture.completedFuture(finishPathing(pathfinderResult));
    }

    protected abstract PathfinderResult findPath(PathPosition start, PathPosition target, PathfinderStrategy strategy);
}
