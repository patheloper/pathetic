package xyz.ollieee.model.finder;

import lombok.NonNull;
import xyz.ollieee.api.pathing.Pathfinder;
import xyz.ollieee.api.pathing.result.Path;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.result.PathfinderSuccess;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import xyz.ollieee.api.wrapper.PathVector;
import xyz.ollieee.model.PathImpl;
import xyz.ollieee.api.wrapper.PathLocation;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class PathfinderImpl implements Pathfinder {
    
    private static final int MAX_CHECKS = 30000;
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    private static final StrategyRegistry STRATEGY_REGISTRY = new StrategyRegistry();
    private static final LinkedHashSet<PathLocation> EMPTY_LINKED_HASHSET = new LinkedHashSet<>();
    private static final Class<? extends PathfinderStrategy> DEFAULT_STRATEGY_TYPE = DirectPathfinderStrategy.class;
    
    private static final PathVector[] OFFSETS = {
            new PathVector(1, 0, 0),
            new PathVector(-1, 0, 0),
            new PathVector(0, 0, 1),
            new PathVector(0, 0, -1),
            new PathVector(0, 1, 0),
            new PathVector(0, -1, 0),
    };

    @NonNull
    @Override
    public PathfinderResult findPath(@NonNull PathLocation start, @NonNull PathLocation target) {
        return findPath(start, target, DEFAULT_STRATEGY_TYPE);
    }

    @NonNull
    @Override
    public PathfinderResult findPath(@NonNull PathLocation start, @NonNull PathLocation target, @NonNull Class<? extends PathfinderStrategy> strategyType) {
        return seekPath(start, target, strategyType);
    }

    @NonNull
    @Override
    public CompletableFuture<PathfinderResult> findPathAsync(@NonNull PathLocation start, @NonNull PathLocation target) {
        return findPathAsync(start, target, DEFAULT_STRATEGY_TYPE);
    }

    @NonNull
    @Override
    public CompletableFuture<PathfinderResult> findPathAsync(@NonNull PathLocation start, @NonNull PathLocation target, @NonNull Class<? extends PathfinderStrategy> strategyType) {
        return CompletableFuture.supplyAsync(() -> seekPath(start, target, strategyType), FORK_JOIN_POOL);
    }

    private @NonNull PathfinderResult seekPath(PathLocation start, PathLocation target, Class<? extends PathfinderStrategy> strategyType) {

        int depth = 1;
        Node startNode = new Node(start.toIntegers(), start.toIntegers(), target.toIntegers(), 0);

        final PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));

        final Set<PathLocation> examinedLocations = new HashSet<>();

        final PathfinderStrategy strategy = STRATEGY_REGISTRY.attemptRegister(strategyType);

        while (!nodeQueue.isEmpty() && depth <= MAX_CHECKS) {

            Node currentNode = nodeQueue.poll();

            if (currentNode.hasReachedEnd()) {
                return new PathfinderResultImpl(PathfinderSuccess.FOUND, retracePath(currentNode));
            }

            evaluateNewNodes(nodeQueue, examinedLocations, strategy, currentNode);

            depth++;

        }

        return null;

    }

    private Path retracePath(@NonNull Node node) {

        LinkedHashSet<PathLocation> path = new LinkedHashSet<>();
        Node currentNode = node;

        while(currentNode != null) {
            path.add(currentNode.getLocation());
            currentNode = currentNode.getParent();
        }

        List<PathLocation> pathReversed = new ArrayList<>(path);

        pathReversed.add(node.getStart());
        Collections.reverse(pathReversed);

        return new PathImpl(node.getStart(), node.getTarget(), new LinkedHashSet<>(pathReversed));
    }

    private void evaluateNewNodes(PriorityQueue<Node> nodeQueue, Set<PathLocation> examinedLocations, PathfinderStrategy strategy, Node currentNode) {

        for (Node neighbourNode : this.getNeighbours(currentNode)) {
            if (this.nodeIsValid(neighbourNode, currentNode, nodeQueue, examinedLocations, strategy)) {
                nodeQueue.add(neighbourNode);
            }
        }
    }

    private Collection<Node> getNeighbours(Node currentNode) {

        final Set<Node> newNodes = new HashSet<>(OFFSETS.length);

        for (PathVector offset : OFFSETS) {

            Node newNode = new Node(currentNode.getLocation().add(offset), currentNode.getStart(), currentNode.getTarget(), currentNode.getDepth() + 1);
            newNode.setParent(currentNode);
            newNodes.add(newNode);
        }

        return newNodes;
    }

    private boolean nodeIsValid(Node node, Node parentNode, PriorityQueue<Node> nodeQueue, Set<PathLocation> examinedLocations, PathfinderStrategy strategy) {

        if (examinedLocations.contains(node.getLocation())) {
            return false;
        }

        if (nodeQueue.contains(node)) {
            return false;
        }

        if (!isWithinWorldBounds(node.getLocation())) {
            return false;
        }

        if (!strategy.isValid(node.getLocation().getBlock(), parentNode.getLocation().getBlock(), node.getParent() == null ? node.getLocation().getBlock() : node.getParent().getLocation().getBlock())) {
            return false;
        }

        return examinedLocations.add(node.getLocation());

    }
    
    private boolean isWithinWorldBounds(PathLocation location) {
        return location.getPathWorld().getMinHeight() < location.getBlockY() && location.getBlockY() < location.getPathWorld().getMaxHeight();
    }
}
