package xyz.ollieee.model.pathing;

import lombok.NonNull;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.event.PathingFinishedEvent;
import xyz.ollieee.api.event.PathingStartFindEvent;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.result.PathfinderState;
import xyz.ollieee.api.pathing.result.progress.ProgressMonitor;
import xyz.ollieee.api.pathing.result.task.PathingTask;
import xyz.ollieee.api.pathing.rules.PathingRuleSet;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.pathing.strategy.StrategyData;
import xyz.ollieee.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathLocation;
import xyz.ollieee.api.wrapper.PathVector;
import xyz.ollieee.bukkit.event.EventPublisher;
import xyz.ollieee.model.pathing.handler.PathfinderAsyncExceptionHandler;
import xyz.ollieee.model.pathing.result.PathImpl;
import xyz.ollieee.model.pathing.result.PathfinderResultImpl;
import xyz.ollieee.util.WatchdogUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

public class PathfinderImpl implements Pathfinder {

    private static final Executor FORK_JOIN_POOL =
            new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                    new PathfinderAsyncExceptionHandler(),
                    true);

    private static final PathfinderStrategy DEFAULT_STRATEGY = new DirectPathfinderStrategy();

    private static final Set<PathLocation> EMPTY_LINKED_HASHSET = Collections.unmodifiableSet(new LinkedHashSet<>());

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

    private static @NonNull PathfinderResult seekPath(PathLocation start, PathLocation target, PathfinderStrategy pathfinderStrategy, PathVector[] offsets, ProgressMonitor progressMonitor, Integer maxIterations, Integer maxPathLength) {

        PathingStartFindEvent startEvent = new PathingStartFindEvent(start, target, pathfinderStrategy);
        EventPublisher.raiseEvent(startEvent);

        if (startEvent.isCancelled())
            return finish(new PathfinderResultImpl(PathfinderState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));

        if (!start.getPathWorld().equals(target.getPathWorld()))
            return finish(new PathfinderResultImpl(PathfinderState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));

        if(start.equals(target)) // could be too accurate
            return finish(new PathfinderResultImpl(PathfinderState.FOUND, new PathImpl(start, target, Collections.singleton(start))));

        Node startNode = new Node(start.floor(), start.floor(), target.floor(), 0);

        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));
        Set<PathLocation> examinedLocations = new HashSet<>();

        int depth = 1;

        while (!nodeQueue.isEmpty() && depth <= maxIterations) {

            if (depth % 500 == 0) WatchdogUtil.tickWatchdog();

            Node currentNode = nodeQueue.poll();

            assert currentNode != null;

            progressMonitor.update(currentNode.getLocation());
            if (currentNode.hasReachedEnd()) {

                Path path = retracePath(currentNode);
                if (path.length() > maxPathLength)
                    return finish(new PathfinderResultImpl(PathfinderState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));
                return finish(new PathfinderResultImpl(PathfinderState.FOUND, path));
            }

            evaluateNewNodes(nodeQueue, examinedLocations, pathfinderStrategy, currentNode, offsets);
            depth++;
        }

        return finish(new PathfinderResultImpl(PathfinderState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));
    }

    private static Path retracePath(@NonNull Node node) {

        List<PathLocation> path = new ArrayList<>();

        Node currentNode = node;
        while(currentNode != null) {
            path.add(currentNode.getLocation());
            currentNode = currentNode.getParent();
        }

        path.add(node.getStart());
        Collections.reverse(path);

        return new PathImpl(node.getStart(), node.getTarget(), path);
    }

    private static void evaluateNewNodes(PriorityQueue<Node> nodeQueue, Set<PathLocation> examinedLocations, PathfinderStrategy strategy, Node currentNode, PathVector[] offsets) {

        for (Node neighbourNode : getNeighbours(currentNode, offsets)) {
            if (nodeIsValid(neighbourNode, nodeQueue, examinedLocations, strategy)) {
                nodeQueue.add(neighbourNode);
            }
        }
    }

    private static Collection<Node> getNeighbours(Node currentNode, PathVector[] offsets) {

        final Set<Node> newNodes = new HashSet<>(offsets.length);

        for (PathVector offset : offsets) {

            Node newNode = new Node(currentNode.getLocation().add(offset), currentNode.getStart(), currentNode.getTarget(), currentNode.getDepth() + 1);
            newNode.setParent(currentNode);
            newNodes.add(newNode);
        }

        return newNodes;
    }

    private static boolean nodeIsValid(Node node, PriorityQueue<Node> nodeQueue, Set<PathLocation> examinedLocations, PathfinderStrategy strategy) {

        if (examinedLocations.contains(node.getLocation())) {
            return false;
        }

        if (nodeQueue.contains(node)) {
            return false;
        }

        if (!isWithinWorldBounds(node.getLocation())) {
            return false;
        }

        SnapshotManager snapshotManager = Pathetic.getSnapshotManager();
        if (!strategy.isValid(new StrategyData(snapshotManager, node.getLocation()))) {
            return false;
        }

        return examinedLocations.add(node.getLocation());

    }

    private static boolean isWithinWorldBounds(PathLocation location) {
        return location.getPathWorld().getMinHeight() < location.getBlockY() && location.getBlockY() < location.getPathWorld().getMaxHeight();
    }

    private static PathfinderResult finish(PathfinderResult pathfinderResult) {
        EventPublisher.raiseEvent(new PathingFinishedEvent(pathfinderResult));
        return pathfinderResult;
    }

    @NonNull
    @Override
    public PathingTask<PathfinderResult> findPath(@NonNull PathLocation start, @NonNull PathLocation target) {
        return findPath(start, target, null);
    }

    @NonNull
    @Override
    public PathingTask<PathfinderResult> findPath(@NonNull PathLocation start, @NonNull PathLocation target, PathingRuleSet rules) {
        return setAndStart(start, target, rules);
    }

    private PathingTask<PathfinderResult> setAndStart(PathLocation start, PathLocation target, PathingRuleSet rules) {

        if (rules == null) {
            rules = PathingRuleSet.builder().build();
        }

        CompletableFuture<PathfinderResult> future;
        PathingRuleSet finalRules = rules;

        int maxIterations = finalRules.getMaxIterations() == 0 ? Integer.MAX_VALUE : finalRules.getMaxIterations();
        int maxPathLength = finalRules.getMaxPathLength() == 0 ? Integer.MAX_VALUE : finalRules.getMaxPathLength();
        PathfinderStrategy strategy = finalRules.getStrategy() == null ? DEFAULT_STRATEGY : finalRules.getStrategy();
        PathVector[] offsets = rules.isAllowDiagonal() ? Stream.of(OFFSETS, CORNER_OFFSETS).flatMap(Stream::of).toArray(PathVector[]::new) : OFFSETS;
        ProgressMonitor progressMonitor = new ProgressMonitor(start, target);

        if (rules.isAsync()) {
            future = CompletableFuture.supplyAsync(() ->
                    seekPath(start, target, strategy, offsets, progressMonitor, maxIterations, maxPathLength), FORK_JOIN_POOL);
        } else {
            future = CompletableFuture.completedFuture(
                    seekPath(start, target, strategy, offsets, progressMonitor, maxIterations, maxPathLength));
        }

        return new PathingTask<>(future, progressMonitor);
    }
}
