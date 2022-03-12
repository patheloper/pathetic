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
   
    private static final LinkedHashSet<PathLocation> EMPTY_LINKED_HASHSET = new LinkedHashSet<>();
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
        
        PathingStartFindEvent pathingStartFindEvent = callStart(start, target, strategy);
        if(pathingStartFindEvent.isCancelled())
            return callFinish(PathfinderSuccess.CANCELLED, new PathImpl(start, target, EMPTY_LINKED_HASHSET));
    
        if (!start.getPathWorld().equals(target.getPathWorld())
                || !strategy.isValid(start.getBlock(), null, null)
                || !strategy.isValid(target.getBlock(), null, null))
            return callFinish(PathfinderSuccess.INVALID, new PathImpl(start, target, EMPTY_LINKED_HASHSET));
        
        if (start.getBlockX() == target.getBlockX() && start.getBlockY() == target.getBlockY() && start.getBlockZ() == target.getBlockZ())
            return callFinish(PathfinderSuccess.FOUND, new PathImpl(start, target, (LinkedHashSet<PathLocation>) Collections.singleton(target)));
    
        Node startNode = new Node(start.add(0.5, 0.5, 0.5), start, target, 0);
        Node targetNode = new Node(target.add(0.5, 0.5, 0.5), start, target, 0);
        
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(startNode);
        
        Optional<Path> pathOptional = processNodeQueue(queue, startNode, start, targetNode, target);
        return pathOptional.map(path -> callFinish(PathfinderSuccess.FOUND, path)).orElseGet(() -> callFinish(PathfinderSuccess.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));
    }
    
    private Optional<Path> processNodeQueue(Queue<Node> queue, Node startNode, PathLocation start, Node targetNode, PathLocation target) {
    
        Set<PathLocation> processed = new HashSet<>();
    
        int depth = 0;
        while (!queue.isEmpty() && depth <= MAX_CHECKS) {
        
            if (Bukkit.isPrimaryThread() && depth % 1000 == 0)
                WatchdogHelper.tickWatchdog();
        
            Node node = queue.poll();
            if(node == null)
                throw new IllegalStateException("Something exploded randomly");
        
            depth++;
        
            if (node.equals(targetNode))
                return Optional.of(retracePath(node, startNode, start, target));

            Optional<Path> pathOptional = processNeighbourNodes(queue, node, startNode, start, targetNode, target, processed);
            if(pathOptional.isPresent())
                return pathOptional;
        }
        
        return Optional.empty();
    }
    
    private Optional<Path> processNeighbourNodes(Queue<Node> queue, Node node, Node startNode, PathLocation start, Node targetNode, PathLocation target, Set<PathLocation> processed) {
    
        for (Node neighbourNode : getNeighbours(node, start, target)) {
        
            if (neighbourNode.equals(targetNode))
                return Optional.of(retracePath(neighbourNode, startNode, start, target));
        
            if(validateNode(queue, neighbourNode, processed))
                queue.add(neighbourNode);
        }
        
        return Optional.empty();
    }
    
    private boolean validateNode(Queue<Node> queue, Node node, Set<PathLocation> processed) {
    
        boolean validateNode = queue.contains(node)
                && queue.removeIf(node1 -> node1.getLocation().equals(node.getLocation())
                && node1.getDepth() > node.getDepth());
    
        boolean verifyLocation = this.verifyLocation(node.getLocation())
                || strategy.isValid(node.getLocation().getBlock(), node.getLocation().getBlock(), node.getParent() == null ? node.getLocation().getBlock() : node.getParent().getLocation().getBlock())
                || processed.add(node.getLocation());
        
        return validateNode || verifyLocation;
    }
    
    private PathingStartFindEvent callStart(PathLocation start, PathLocation target, PathfinderStrategy strategy) {
        
        PathingStartFindEvent pathingStartFindEvent = new PathingStartFindEvent(start, target, strategy);
        EventUtil.callEvent(pathingStartFindEvent);
    
        BStatsHandler.increasePathCount();
    
        return pathingStartFindEvent;
    }
    
    private PathfinderResult callFinish(PathfinderSuccess success, Path path) {
        
        PathfinderResult pathfinderResult = new PathfinderResultImpl(success, path);
        EventUtil.callEvent(new PathingFinishedEvent(pathfinderResult));
        
        if(success != PathfinderSuccess.FOUND)
            BStatsHandler.increaseFailedPathCount();
    
        return pathfinderResult;
    }
    
    private Path retracePath(Node endNode, Node startNode, PathLocation start, PathLocation target) {
        
        LinkedHashSet<PathLocation> path = new LinkedHashSet<>();
        
        Node currentNode = endNode;
        while (!currentNode.equals(startNode)) {
            
            path.add(currentNode.getLocation());
            
            if (currentNode.getParent() == null)
                break;
            
            currentNode = currentNode.getParent();
        }
        
        return new PathImpl(start, target, new LinkedHashSet<>(reverseAndConvertPathContentCollection(path, start)));
    }
    
    private List<PathLocation> reverseAndConvertPathContentCollection(Collection<PathLocation> collection, PathLocation start) {
        
        List<PathLocation> pathReversed = new ArrayList<>(collection);
        pathReversed.add(start);
    
        Collections.reverse(pathReversed);
        BStatsHandler.addLength(pathReversed.size());
        
        return pathReversed;
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
