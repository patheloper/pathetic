package xyz.oli.pathing.model.finder;

import lombok.NonNull;
import org.bukkit.Bukkit;
import xyz.oli.api.event.PathingFinishedEvent;
import xyz.oli.api.event.PathingStartFindEvent;
import xyz.oli.api.pathing.Pathfinder;
import xyz.oli.api.pathing.result.Path;
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
    
    private static LinkedHashSet<PathLocation> line(final PathLocation start, final PathLocation end) {
        
        final PathVector fullLine = end.toVector().subtract(start.toVector());
        final double fullLength = fullLine.length();
        final PathVector deltaLine = fullLine.clone().normalize();
        
        final List<PathLocation> locations = new ArrayList<>((int) (fullLength));
        
        locations.add(start);
        
        for (int i = 0; i < (int) (fullLength); i++) {
            final PathLocation nextLocation = locations.get(locations.size() - 1).clone().add(deltaLine);
            locations.add(nextLocation);
        }
        
        locations.remove(start);
        locations.add(end);
        
        return new LinkedHashSet<>(locations);
    }
    
    private PathfinderStrategy strategy = DEFAULT_STRATEGY;
    
    @Override
    public PathfinderResult findPath(PathLocation start, PathLocation target) {
        return seekPath(start, target, strategy);
    }
    
    @Override
    public CompletableFuture<PathfinderResult> findPathAsync(PathLocation start, PathLocation target) {
        return CompletableFuture.supplyAsync(() -> seekPath(start, target, strategy), FORK_JOIN_POOL);
    }
    
    @Override
    public Pathfinder setStrategy(@NonNull PathfinderStrategy strategy) {
        this.strategy = strategy;
        return this;
    }
    
    private @NonNull PathfinderResult seekPath(PathLocation start, PathLocation target, PathfinderStrategy strategy) {
        
        BStatsHandler.increasePathCount();
        
        PathingStartFindEvent pathingStartFindEvent = new PathingStartFindEvent(start, target, strategy);
        EventUtil.callEvent(pathingStartFindEvent);
        
        if (!start.getPathWorld().equals(target.getPathWorld()) || !strategy.isValid(start.getBlock(), null, null) || !strategy.isValid(target.getBlock(), null, null) || pathingStartFindEvent.isCancelled()) {
            BStatsHandler.increaseFailedPathCount();
            return callFinish(PathfinderSuccess.INVALID, new PathImpl(start, target, EMPTY_SET));
        }
        
        if (start.getBlockX() == target.getBlockX() && start.getBlockY() == target.getBlockY() && start.getBlockZ() == target.getBlockZ()) {
            LinkedHashSet<PathLocation> nodeList = new LinkedHashSet<>();
            nodeList.add(target);
            
            return callFinish(PathfinderSuccess.FOUND, new PathImpl(start, target, nodeList));
        }
    
        Optional<PathLocation> firstObstacleLocation = LOSHelper.findFirstObstacle(start, target);
        if (!firstObstacleLocation.isPresent())
            return callFinish(PathfinderSuccess.FOUND, new PathImpl(start, target, line(start, target)));
    
        Node startNode = new Node(firstObstacleLocation.get(), start, target, 0);
        Node targetNode = new Node(new PathLocation(target.getPathWorld(), target.getBlockX() + 0.5, target.getBlockY(), target.getBlockZ() + 0.5), start, target, 0);
        
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(startNode);
        
        Set<PathLocation> processed = new HashSet<>();
        
        int depth = 0;
        while (!queue.isEmpty() && depth <= MAX_CHECKS) {
            
            if (Bukkit.isPrimaryThread() && depth % 1000 == 0)
                WatchdogHelper.tickWatchdog();
            
            Node node = queue.poll();
            
            depth++;
            
            if (node.equals(targetNode)) {
                return callFinish(PathfinderSuccess.FOUND, retracePath(node, startNode, start, target));
            }
            
            for (Node neighbourNode : getNeighbours(node, start, target)) {
                
                if (neighbourNode.equals(targetNode)) {
                    return callFinish(PathfinderSuccess.FOUND, retracePath(neighbourNode, startNode, start, target));
                }
                
                if (queue.contains(neighbourNode)) {
                    if (queue.removeIf(node1 -> node1.getLocation().equals(neighbourNode.getLocation()) && node1.getDepth() > neighbourNode.getDepth())) {
                        queue.add(neighbourNode);
                        continue;
                    }
                }
                
                if (!this.verifyLocation(neighbourNode.getLocation()) || !strategy.isValid(neighbourNode.getLocation().getBlock(),
                        node.getLocation().getBlock(),
                        node.getParent() == null ? node.getLocation().getBlock() : node.getParent().getLocation().getBlock()) || !processed.add(neighbourNode.getLocation()))
                    continue;
                
                queue.add(neighbourNode);
            }
        }
        
        BStatsHandler.increaseFailedPathCount();
        
        return callFinish(PathfinderSuccess.FAILED, new PathImpl(start, target, EMPTY_SET));
    }
    
    private PathfinderResult callFinish(PathfinderSuccess success, Path path) {
        
        PathfinderResult pathfinderResult = new PathfinderResultImpl(success, path);
        EventUtil.callEvent(new PathingFinishedEvent(pathfinderResult));
        
        return pathfinderResult;
    }
    
    private Path retracePath(Node endNode, Node startNode, PathLocation start, PathLocation target) {
        
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
        
        return new PathImpl(start, target, new LinkedHashSet<>(pathReversed));
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
    
    private boolean verifyLocation(PathLocation location) {
        return location.getPathWorld().getMinHeight() < location.getBlockY() && location.getBlockY() < location.getPathWorld().getMaxHeight();
    }
}
