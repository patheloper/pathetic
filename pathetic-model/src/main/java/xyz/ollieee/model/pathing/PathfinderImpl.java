package xyz.ollieee.model.pathing;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import xyz.ollieee.api.event.PathingFinishedEvent;
import xyz.ollieee.api.event.PathingStartFindEvent;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.result.PathState;
import xyz.ollieee.api.pathing.result.progress.ProgressMonitor;
import xyz.ollieee.api.pathing.result.task.PathingTask;
import xyz.ollieee.api.pathing.rules.PathingRuleSet;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathBlock;
import xyz.ollieee.api.wrapper.PathLocation;
import xyz.ollieee.api.wrapper.PathVector;
import xyz.ollieee.bukkit.event.EventPublisher;
import xyz.ollieee.model.pathing.handler.PathfinderAsyncExceptionHandler;
import xyz.ollieee.model.pathing.result.PathImpl;
import xyz.ollieee.model.pathing.result.PathfinderResultImpl;
import xyz.ollieee.model.snapshot.FailingSnapshotManager;
import xyz.ollieee.util.WatchdogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

@RequiredArgsConstructor(onConstructor = @__(@NonNull))
public class PathfinderImpl implements Pathfinder {

    private static final Executor FORK_JOIN_POOL =
            new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                    new PathfinderAsyncExceptionHandler(),
                    true);

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

    private @NonNull PathfinderResult seekPath(PathLocation start, PathLocation target, PathVector[] offsets, ProgressMonitor progressMonitor) {

        PathingStartFindEvent startEvent = new PathingStartFindEvent(start, target, ruleSet.getStrategy());
        EventPublisher.raiseEvent(startEvent);

        if (startEvent.isCancelled())
            return finish(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));

        if (!start.getPathWorld().equals(target.getPathWorld()))
            return finish(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));

        if (start.isInSameBlock(target))
            return finish(new PathfinderResultImpl(PathState.FOUND, new PathImpl(start, target, Collections.singleton(start))));

        if (ruleSet.isAllowingFailFast() && isTargetUnreachable(target, offsets))
            return finish(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));

        if (ruleSet.isAllowingAlternateTarget() && isTargetUnreachable(target, offsets))
            target = bubbleSearch(target, offsets).getPathLocation();

        Node startNode = new Node(start.floor(), start.floor(), target.floor(), 0);

        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));
        Set<PathLocation> examinedLocations = new HashSet<>();

        int depth = 1;
        Node lastEverFound = null;

        while (!nodeQueue.isEmpty() && depth <= ruleSet.getMaxIterations()) {

            if (depth % 500 == 0) WatchdogUtil.tickWatchdog();

            Node currentNode = nodeQueue.poll();
            if(lastEverFound == null)
                lastEverFound = currentNode;

            if (currentNode == null)
                throw new IllegalStateException("Something just exploded");

            progressMonitor.update(currentNode.getLocation());

            if(currentNode.heuristic() < lastEverFound.heuristic())
                lastEverFound = currentNode;

            if(ruleSet.getMaxLength() > 0 && getLength(lastEverFound) >= ruleSet.getMaxLength())
                return finish(new PathfinderResultImpl(PathState.FOUND, retracePath(lastEverFound)));

            if (currentNode.hasReachedEnd()) {
                Path path = retracePath(lastEverFound);
                return finish(new PathfinderResultImpl(PathState.FOUND, path));
            }

            evaluateNewNodes(nodeQueue, examinedLocations, currentNode, offsets, ruleSet.getStrategy());
            depth++;
        }

        if (ruleSet.isAllowingFallback() && lastEverFound != null)
            return finish(new PathfinderResultImpl(PathState.FALLBACK, retracePath(lastEverFound)));

        return finish(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));
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

    private PathfinderResult finish(PathfinderResult pathfinderResult) {

        ruleSet.getStrategy().cleanup();
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

        PathVector[] offsets = ruleSet.isAllowingDiagonal() ? BOTH_OFFSETS : OFFSETS;
        ProgressMonitor progressMonitor = new ProgressMonitor(start, target);

        CompletableFuture<PathfinderResult> future;
        if (this.ruleSet.isAsync()) {
            future = CompletableFuture.supplyAsync(() ->
                    seekPath(start, target, offsets, progressMonitor), FORK_JOIN_POOL);
        } else {
            future = CompletableFuture.completedFuture(
                    seekPath(start, target, offsets, progressMonitor));
        }

        return new PathingTask(future, progressMonitor);
    }
}
