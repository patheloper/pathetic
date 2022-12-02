package org.patheloper.model.pathing;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.NonNull;
import org.bukkit.event.Cancellable;
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
import org.patheloper.model.exception.PathfinderSpecificException;
import org.patheloper.model.pathing.handler.PathfinderAsyncExceptionHandler;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.model.snapshot.FailingSnapshotManager;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractPathfinder implements Pathfinder {

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

        PathingStartFindEvent startEvent = new PathingStartFindEvent(start, target, strategy);
        EventPublisher.raiseEvent(startEvent);

        if(initialChecksFailed(start, target, startEvent))
            return CompletableFuture.completedFuture(
                    PathingHelper.finishPathing(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET))));

        if (pathingRuleSet.isAllowingAlternateTarget() && isTargetUnreachable(target))
            target = PathingHelper.bubbleSearchAlternative(target, offset, snapshotManager).getPathPosition(); // TODO: this is always sync, maybe unwanted.

        return producePathing(start, target, strategy);
    }

    protected SnapshotManager getSnapshotManager() {
        return snapshotManager;
    }

    private boolean initialChecksFailed(PathPosition start, PathPosition target, Cancellable startEvent) {

        // Do some initial checks to make sure that we should even bother with pathfinding
        if (startEvent.isCancelled())
            return true;

        if (!start.getPathEnvironment().equals(target.getPathEnvironment()))
            return true;

        if (start.isInSameBlock(target))
            return true;

        return this.pathingRuleSet.isAllowingFailFast() && isTargetUnreachable(target) || isStartNotEscapable(start);
    }

    private boolean isTargetUnreachable(PathPosition target) {

        for(PathVector vector : offset.getVectors()) {

            PathPosition offsetPosition = target.add(vector);
            PathBlock pathBlock = this.getSnapshotManager().getBlock(offsetPosition);

            if(pathBlock.isPassable())
                return false;
        }

        return true;
    }

    private boolean isStartNotEscapable(PathPosition start) {

        for(PathVector vector : offset.getVectors()) {

            PathPosition offsetPosition = start.add(vector);
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
            throw new PathfinderSpecificException("Failed to instantiate PathfinderStrategy", e);
        }
    }

    private CompletionStage<PathfinderResult> producePathing(PathPosition start, PathPosition target, PathfinderStrategy strategy) {

        if(pathingRuleSet.isAsync()) {
            @NonNull PathPosition finalTarget = target;
            return CompletableFuture.supplyAsync(() -> findPath(start, finalTarget, strategy), PATHING_EXECUTOR);
        }

        PathfinderResult pathfinderResult = findPath(start, target, strategy);
        return CompletableFuture.completedFuture(PathingHelper.finishPathing(pathfinderResult));
    }

    protected abstract PathfinderResult findPath(PathPosition start, PathPosition target, PathfinderStrategy strategy);
}
