package org.patheloper.model.pathing;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.patheloper.api.event.PathingFinishedEvent;
import org.patheloper.api.event.PathingStartFindEvent;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.result.progress.ProgressMonitor;
import org.patheloper.api.pathing.result.task.PathingTask;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathLocation;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.bukkit.event.EventPublisher;
import org.patheloper.model.pathing.handler.PathfinderAsyncExceptionHandler;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.model.snapshot.FailingSnapshotManager;
import org.patheloper.util.WatchdogUtil;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@RequiredArgsConstructor(onConstructor = @__(@NonNull))
public class PathfinderImpl implements Pathfinder {

    private static final ExecutorService FIXED_POOL =
            new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors() / 4,
                    Runtime.getRuntime().availableProcessors(),
                    250L, // or we let them die instantly
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(1000), // 1000 pathing-tasks allowed in queue
                    new ThreadFactoryBuilder()
                            .setUncaughtExceptionHandler(new PathfinderAsyncExceptionHandler())
                            .setDaemon(true)
                            .setNameFormat("Pathfinder Task-%d")
                            .build(),
                    new ThreadPoolExecutor.DiscardOldestPolicy()); // TODO: own policy

    private static final Set<PathLocation> EMPTY_LINKED_HASHSET = Collections.unmodifiableSet(new LinkedHashSet<>(0));

    private static final PathVector[] OFFSETS = {
            new PathVector(1, 0, 0),
            new PathVector(-1, 0, 0),
            new PathVector(0, 0, 1),
            new PathVector(0, 0, -1),
            new PathVector(0, 1, 0),
            new PathVector(0, -1, 0),
    };

    private static final PathVector[] CORNER_OFFSETS = {
            new PathVector(1, 0, 1), new PathVector(-1, 0, -1), new PathVector(-1, 0, 1), new PathVector(1, 0, -1),
            new PathVector(0, 1, 1), new PathVector(1, 1, 1), new PathVector(1, 1, 0), new PathVector(1, 1, -1),
            new PathVector(0, 1, -1), new PathVector(-1, 1, -1), new PathVector(-1, 1, 0), new PathVector(-1, 1, 1),
            new PathVector(0, -1, 1), new PathVector(1, -1, 1), new PathVector(1, -1, 0), new PathVector(1, -1, -1),
            new PathVector(0, -1, -1), new PathVector(-1, -1, -1), new PathVector(-1, -1, 0), new PathVector(-1, -1, 1),
    };

    private static final PathVector[] BOTH_OFFSETS = Stream.concat(Stream.of(OFFSETS), Stream.of(CORNER_OFFSETS))
            .toArray(PathVector[]::new);

    private static final SnapshotManager SIMPLE_SNAPSHOT_MANAGER = new FailingSnapshotManager();
    private static final SnapshotManager LOADING_SNAPSHOT_MANAGER = new FailingSnapshotManager.RequestingSnapshotManager();

    private static Path retracePath(@NonNull Node node) {

        List<PathLocation> path = new ArrayList<>();

        Node currentNode = node;
        while (currentNode != null) {
            path.add(currentNode.getLocation());
            currentNode = currentNode.getParent();
        }

        path.add(node.getStart());
        Collections.reverse(path);

        return new PathImpl(node.getStart(), node.getTarget(), path);
    }

    private static int getLength(Node node) {

        int length = 0;

        Node currentNode = node;
        while (currentNode != null) {
            length++;
            currentNode = currentNode.getParent();
        }

        return length;
    }

    private static boolean isWithinWorldBounds(PathLocation location) {
        return location.getPathWorld().getMinHeight() < location.getBlockY() && location.getBlockY() < location.getPathWorld().getMaxHeight();
    }

    private static Collection<Node> getNeighbours(Node currentNode, PathVector[] offsets) {

        Set<Node> newNodes = new HashSet<>(offsets.length);

        for (PathVector offset : offsets) {

            Node newNode = new Node(currentNode.getLocation().add(offset), currentNode.getStart(), currentNode.getTarget(), currentNode.getDepth() + 1);
            newNode.setParent(currentNode);

            newNodes.add(newNode);
        }

        return newNodes;
    }

    private final PathingRuleSet ruleSet;

    @NonNull
    @Override
    public PathingTask findPath(@NonNull PathLocation start, @NonNull PathLocation target) {
        return setAndStart(start, target);
    }

    private @NonNull PathfinderResult seekPath(PathLocation start, PathLocation target, PathfinderStrategy strategy,
                                               PathVector[] offsets, ProgressMonitor progressMonitor) {

        PathingStartFindEvent startEvent = new PathingStartFindEvent(start, target, strategy);
        EventPublisher.raiseEvent(startEvent);

        // Do some initial checks to make sure that we should even bother with pathfinding
        if (startEvent.isCancelled())
            return finish(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)), strategy);

        if (!start.getPathWorld().equals(target.getPathWorld()))
            return finish(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)), strategy);

        if (start.isInSameBlock(target))
            return finish(new PathfinderResultImpl(PathState.FOUND, new PathImpl(start, target, Collections.singleton(start))), strategy);

        if (this.ruleSet.isAllowingFailFast() && isTargetUnreachable(target, offsets))
            return finish(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)), strategy);

        if (this.ruleSet.isAllowingAlternateTarget() && isTargetUnreachable(target, offsets))
            target = bubbleSearch(target, offsets).getPathLocation();

        // Create the initial node
        Node startNode = new Node(start.floor(), start.floor(), target.floor(), 0);

        // Create the open and closed sets
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));
        Set<PathLocation> examinedLocations = new HashSet<>();

        // This is the current depth of the search and the last node
        int depth = 1;
        Node lastEverFound = null;

        while (!nodeQueue.isEmpty() && depth <= this.ruleSet.getMaxIterations()) {

            // Every 500 iterations, tick the watchdog so that a watchdog timeout doesn't occur
            if (depth % 500 == 0) WatchdogUtil.tickWatchdog();

            // Get the next node from the queue
            Node currentNode = nodeQueue.poll();
            if (lastEverFound == null)
                lastEverFound = currentNode;

            if (currentNode == null)
                throw new IllegalStateException("Something just exploded");

            // Update the progress monitor for the pathfinding attempt with the current node
            progressMonitor.update(currentNode.getLocation());

            if(currentNode.heuristic() < lastEverFound.heuristic())
                lastEverFound = currentNode;

            // Check to see if we have reached the length limit
            if (this.ruleSet.getMaxLength() > 0 && getLength(lastEverFound) >= this.ruleSet.getMaxLength())
                return finish(new PathfinderResultImpl(PathState.FOUND, retracePath(lastEverFound)), strategy);

            // This means that the current node is the target, so we can stop here
            if (currentNode.hasReachedEnd())
                return finish(new PathfinderResultImpl(PathState.FOUND, retracePath(lastEverFound)), strategy);

            evaluateNewNodes(nodeQueue, examinedLocations, currentNode, offsets, strategy);
            depth++;
        }

        if (this.ruleSet.isAllowingFallback() && lastEverFound != null)
            return finish(new PathfinderResultImpl(PathState.FALLBACK, retracePath(lastEverFound)), strategy);

        return finish(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)), strategy);
    }

    private void evaluateNewNodes(PriorityQueue<Node> nodeQueue, Set<PathLocation> examinedLocations, Node currentNode, PathVector[] offsets, PathfinderStrategy strategy) {
        for (Node neighbourNode : getNeighbours(currentNode, offsets))
            if (nodeIsValid(neighbourNode, nodeQueue, examinedLocations, strategy))
                nodeQueue.add(neighbourNode);
    }

    private boolean nodeIsValid(Node node, PriorityQueue<Node> nodeQueue, Set<PathLocation> examinedLocations, PathfinderStrategy strategy) {

        if (examinedLocations.contains(node.getLocation()))
            return false;

        if (nodeQueue.contains(node))
            return false;

        if (!isWithinWorldBounds(node.getLocation()))
            return false;

        if (!strategy.isValid(node.getLocation(), this.getSnapshotManager()))
            return false;

        return examinedLocations.add(node.getLocation());
    }

    private PathfinderResult finish(PathfinderResult pathfinderResult, PathfinderStrategy strategy) {
        EventPublisher.raiseEvent(new PathingFinishedEvent(pathfinderResult));
        return pathfinderResult;
    }

    private boolean isTargetUnreachable(PathLocation target, PathVector[] offsets) {

        for(PathVector offset : offsets) {

            PathLocation offsetLocation = target.add(offset);
            PathBlock pathBlock = this.getSnapshotManager().getBlock(offsetLocation);

            if(pathBlock.isPassable())
                return false;
        }

        return true;
    }

    /*
     * Bloating up like a bubble until a reachable block is found
     * The block itself might not be passable, but at least reachable from the outside
     *
     * NOTE: The reachable block is not guaranteed to be the closest reachable block
     */
    private PathBlock bubbleSearch(PathLocation target, PathVector[] offsets) {

        Set<PathLocation> examinedLocations = new HashSet<>();
        Set<PathLocation> newLocations = new HashSet<>();

        newLocations.add(target);

        while (!newLocations.isEmpty()) {

            Set<PathLocation> nextLocations = new HashSet<>();
            for (PathLocation location : newLocations) {

                for (PathVector offset : offsets) {

                    PathLocation offsetLocation = location.add(offset);
                    PathBlock pathBlock = this.getSnapshotManager().getBlock(offsetLocation);

                    if (pathBlock.isPassable() && !pathBlock.getPathLocation().isInSameBlock(target))
                        return pathBlock;

                    if (!examinedLocations.contains(offsetLocation))
                        nextLocations.add(offsetLocation);
                }
                examinedLocations.add(location);
            }
            newLocations = nextLocations;
        }

        return this.getSnapshotManager().getBlock(target);
    }

    private SnapshotManager getSnapshotManager() {
        return this.ruleSet.isLoadingChunks() ? LOADING_SNAPSHOT_MANAGER : SIMPLE_SNAPSHOT_MANAGER;
    }

    private PathingTask setAndStart(PathLocation start, PathLocation target) {

        PathVector[] offsets = this.ruleSet.isAllowingDiagonal() ? BOTH_OFFSETS : OFFSETS;
        PathfinderStrategy strategy;
        try {
            strategy = this.ruleSet.getStrategy().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }

        ProgressMonitor progressMonitor = new ProgressMonitor(start, target);

        CompletableFuture<PathfinderResult> future;
        if (this.ruleSet.isAsync()) {
            future = CompletableFuture.supplyAsync(() ->
                    seekPath(start, target, strategy, offsets, progressMonitor), FIXED_POOL);
        } else {
            future = CompletableFuture.completedFuture(
                    seekPath(start, target, strategy, offsets, progressMonitor));
        }

        return new PathingTask(future, progressMonitor);
    }
}
