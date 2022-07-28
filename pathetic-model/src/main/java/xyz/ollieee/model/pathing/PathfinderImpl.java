package xyz.ollieee.model.pathing;

import lombok.NonNull;
import xyz.ollieee.Pathetic;
import xyz.ollieee.api.event.PathingFinishedEvent;
import xyz.ollieee.api.event.PathingStartFindEvent;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.result.PathfinderSuccess;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.pathing.strategy.StrategyEssentialsDao;
import xyz.ollieee.bukkit.event.EventPublisher;
import xyz.ollieee.model.pathing.handler.PathfinderAsyncExceptionHandler;
import xyz.ollieee.model.pathing.result.PathfinderResultImpl;
import xyz.ollieee.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import xyz.ollieee.api.snapshot.SnapshotManager;
import xyz.ollieee.api.wrapper.PathVector;
import xyz.ollieee.model.pathing.result.PathImpl;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class PathfinderImpl implements Pathfinder {

    private static final Executor FORK_JOIN_POOL =
            new ForkJoinPool(Runtime.getRuntime().availableProcessors(),
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory,
                    new PathfinderAsyncExceptionHandler(),
                    true);

    private static final PathfinderStrategy DEFAULT_STRATEGY = new DirectPathfinderStrategy();

    private static final Set<PathLocation> EMPTY_LINKED_HASHSET = Collections.unmodifiableSet(new LinkedHashSet<PathLocation>());

    private static final PathVector[] OFFSETS = {
            new PathVector(1, 0, 0),
            new PathVector(-1, 0, 0),
            new PathVector(0, 0, 1),
            new PathVector(0, 0, -1),
            new PathVector(0, 1, 0),
            new PathVector(0, -1, 0),
    };

    private static @NonNull PathfinderResult seekPath(PathLocation start, PathLocation target, PathfinderStrategy pathfinderStrategy) {

        PathingStartFindEvent startEvent = new PathingStartFindEvent(start, target, pathfinderStrategy);
        EventPublisher.raiseEvent(startEvent);

        if(startEvent.isCancelled())
            return finish(new PathfinderResultImpl(PathfinderSuccess.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));
        // TODO: 27/07/2022 Make a PathfinderResultBuilder to avoid this boilerplate shit

        if(!start.getPathWorld().equals(target.getPathWorld()))
            return finish(new PathfinderResultImpl(PathfinderSuccess.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));

        if(start.equals(target)) // could be too accurate
            return finish(new PathfinderResultImpl(PathfinderSuccess.FOUND, new PathImpl(start, target, Collections.singleton(start))));

        Node startNode = new Node(start.toIntegers(), start.toIntegers(), target.toIntegers(), 0);

        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));
        Set<PathLocation> examinedLocations = new HashSet<>();

        int depth = 1;
        int maxDepth = (int) (100 * start.distance(target));

        while (!nodeQueue.isEmpty() && depth <= maxDepth) {

            Node currentNode = nodeQueue.poll();

            if (currentNode.hasReachedEnd())
                return finish(new PathfinderResultImpl(PathfinderSuccess.FOUND, retracePath(currentNode)));

            evaluateNewNodes(nodeQueue, examinedLocations, pathfinderStrategy, currentNode);
            depth++;
        }

        return finish(new PathfinderResultImpl(PathfinderSuccess.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));
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

    private static void evaluateNewNodes(PriorityQueue<Node> nodeQueue, Set<PathLocation> examinedLocations, PathfinderStrategy strategy, Node currentNode) {

        for (Node neighbourNode : getNeighbours(currentNode)) {
            if (nodeIsValid(neighbourNode, nodeQueue, examinedLocations, strategy)) {
                nodeQueue.add(neighbourNode);
            }
        }
    }

    private static Collection<Node> getNeighbours(Node currentNode) {

        final Set<Node> newNodes = new HashSet<>(OFFSETS.length);

        for (PathVector offset : OFFSETS) {

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
        if (!strategy.isValid(new StrategyEssentialsDao(snapshotManager, node.getLocation()))) {
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
    public PathfinderResult findPath(@NonNull PathLocation start, @NonNull PathLocation target) {
        return findPath(start, target, DEFAULT_STRATEGY);
    }

    @NonNull
    @Override
    public PathfinderResult findPath(@NonNull PathLocation start, @NonNull PathLocation target, @NonNull PathfinderStrategy pathfinderStrategy) {
        return seekPath(start, target, pathfinderStrategy);
    }

    @NonNull
    @Override
    public CompletableFuture<PathfinderResult> findPathAsync(@NonNull PathLocation start, @NonNull PathLocation target) {
        return findPathAsync(start, target, DEFAULT_STRATEGY);
    }

    @NonNull
    @Override
    public CompletableFuture<PathfinderResult> findPathAsync(@NonNull PathLocation start, @NonNull PathLocation target, @NonNull PathfinderStrategy pathfinderStrategy) {
        return CompletableFuture.supplyAsync(() -> seekPath(start, target, pathfinderStrategy), FORK_JOIN_POOL);
    }
}
