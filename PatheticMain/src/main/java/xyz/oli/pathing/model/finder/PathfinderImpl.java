package xyz.oli.pathing.model.finder;

import lombok.NonNull;
import xyz.oli.api.event.PathingFinishedEvent;
import xyz.oli.api.event.PathingStartFindEvent;
import xyz.oli.api.pathing.Pathfinder;
import xyz.oli.api.pathing.result.PathfinderResult;
import xyz.oli.api.pathing.result.PathfinderSuccess;
import xyz.oli.api.pathing.strategy.PathfinderStrategy;
import xyz.oli.api.pathing.strategy.strategies.DirectPathfinderStrategy;
import xyz.oli.api.wrapper.PathVector;
import xyz.oli.pathing.bstats.BStatsHandler;
import xyz.oli.pathing.model.PathImpl;
import xyz.oli.pathing.util.EventUtil;
import xyz.oli.api.wrapper.PathLocation;
import xyz.oli.pathing.util.WatchdogHelper;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class PathfinderImpl implements Pathfinder {
    
    private static final int MAX_CHECKS = 30000;
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    private static final LinkedHashSet<PathLocation> EMPTY_SET = new LinkedHashSet<>();
    private static final PathfinderStrategy DEFAULT_STRATEGY = new DirectPathfinderStrategy();
    
    private static final PathVector[] OFFSETS = {
            
            new PathVector(1, 0, 0),
            new PathVector(-1, 0, 0),
            new PathVector(0, 0, 1),
            new PathVector(0, 0, -1),
            new PathVector(0, 1, 0),
            new PathVector(0, -1, 0),
    };

    private PathfinderStrategy strategy = DEFAULT_STRATEGY;

    @Override
    public Pathfinder setStrategy(@NonNull PathfinderStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public PathfinderResult findPath(PathLocation start, PathLocation target) {
        return seekPath(start, target, strategy);
    }
    
    @Override
    public CompletableFuture<PathfinderResult> findPathAsync(PathLocation start, PathLocation target) {
        return CompletableFuture.supplyAsync(() -> seekPath(start, target, strategy), FORK_JOIN_POOL);
    }
    
    private PathfinderResult seekPath(PathLocation start, PathLocation target, PathfinderStrategy strategy) {
        
        BStatsHandler.increasePathCount();
        
        PathingStartFindEvent pathingStartFindEvent = new PathingStartFindEvent(start, target, strategy);
        EventUtil.callEvent(pathingStartFindEvent);
        
        if (!start.getPathWorld().equals(target.getPathWorld()) || !strategy.isValid(start.getBlock(), null, null) || !strategy.isValid(target.getBlock(), null, null) || pathingStartFindEvent.isCancelled()) {
            
            BStatsHandler.increaseFailedPathCount();
            
            PathfinderResultImpl pathfinderResult = new PathfinderResultImpl(PathfinderSuccess.INVALID, new PathImpl(start, target, EMPTY_SET));
            EventUtil.callEvent(new PathingFinishedEvent(pathfinderResult));
            
            return pathfinderResult;
        }
        
        if (start.getBlockX() == target.getBlockX() && start.getBlockY() == target.getBlockY() && start.getBlockZ() == target.getBlockZ()) {
            
            LinkedHashSet<PathLocation> nodeList = new LinkedHashSet<>();
            nodeList.add(target);
            
            PathfinderResultImpl pathfinderResult = new PathfinderResultImpl(PathfinderSuccess.FOUND, new PathImpl(start, target, nodeList));
            EventUtil.callEvent(new PathingFinishedEvent(pathfinderResult));
            
            return pathfinderResult;
        }
        
        Node startNode = new Node(new PathLocation(start.getPathWorld(), start.getBlockX() + 0.5, start.getBlockY(), start.getBlockZ() + 0.5), start, target, 0);
        Node targetNode = new Node(new PathLocation(target.getPathWorld(), target.getBlockX() + 0.5, target.getBlockY(), target.getBlockZ() + 0.5), start, target, 0);
        
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(startNode);
        
        Set<PathLocation> processed = new HashSet<>();
        
        int depth = 0;
        while (!queue.isEmpty() && depth <= MAX_CHECKS) {

            if (depth % 1000 == 0)
                WatchdogHelper.tickWatchdog();
            
            Node node = queue.poll();

            depth++;

            if (node.equals(targetNode)) {
                return checkTarget(node, startNode, start, target);
            }
            
            for (Node neighbourNode : getNeighbours(node, start, target)) {

                if (neighbourNode.equals(targetNode)) {
                    return checkTarget(neighbourNode, startNode, start, target);
                }

                if (queue.contains(neighbourNode)) {
                    if (queue.removeIf(node1 -> node1.getLocation().equals(neighbourNode.getLocation()) && node1.getDepth() > neighbourNode.getDepth())) {
                        queue.add(neighbourNode);
                        continue;
                    }
                }
                
                if (!strategy.isValid(neighbourNode.getLocation().getBlock(),
                        node.getLocation().getBlock(),
                        node.getParent() == null ? node.getLocation().getBlock() : node.getParent().getLocation().getBlock()) || !processed.add(neighbourNode.getLocation()))
                    continue;

                queue.add(neighbourNode);
            }
        }
        
        BStatsHandler.increaseFailedPathCount();
        
        PathfinderResultImpl pathfinderResult = new PathfinderResultImpl(PathfinderSuccess.FAILED, new PathImpl(start, target, EMPTY_SET));
        EventUtil.callEvent(new PathingFinishedEvent(pathfinderResult));
        
        return pathfinderResult;
    }

    private PathfinderResult checkTarget(Node currentNode, Node startNode, PathLocation start, PathLocation target) {
        PathfinderResult result = retracePath(startNode, currentNode, start, target);
        EventUtil.callEvent(new PathingFinishedEvent(result));

        return result;
    }
    
    private PathfinderResult retracePath(Node startNode, Node endNode, PathLocation start, PathLocation target) {
        
        LinkedHashSet<PathLocation> path = new LinkedHashSet<>();
        Node currentNode = endNode;
        
        while (!currentNode.equals(startNode)) {
            
            path.add(currentNode.getLocation());
            if (currentNode.getParent() == null) break;
            currentNode = currentNode.getParent();
        }
        
        List<PathLocation> pathReversed = new ArrayList<>(path);

        pathReversed.add(start);
        Collections.reverse(pathReversed);

        BStatsHandler.addLength(pathReversed.size());

        PathfinderResultImpl pathfinderResult = new PathfinderResultImpl(PathfinderSuccess.FOUND, new PathImpl(start, target, new LinkedHashSet<>(pathReversed)));
        EventUtil.callEvent(new PathingFinishedEvent(pathfinderResult));
        return pathfinderResult;
    }
    
    private Collection<Node> getNeighbours(Node node, PathLocation start, PathLocation target) {
        
        ArrayList<Node> neighbours = new ArrayList<>(OFFSETS.length);
        
        for (PathVector offset : OFFSETS) {
            
            PathVector midpoint = new PathVector();
            
            if (offset.getY() != 0)
                midpoint.setX(offset.getX() / 2).setZ(offset.getZ() / 2);
            
            Node neighbourNode = new Node(node.getLocation().add(offset).add(midpoint), start, target, node.getDepth() + 1);
            neighbourNode.setParent(node);
            
            neighbours.add(neighbourNode);
        }
        
        return neighbours;
    }
}
