package xyz.oli.pathing.model.pathing.finder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import xyz.oli.api.event.PathingFinishedEvent;
import xyz.oli.api.event.PathingStartFindEvent;
import xyz.oli.api.pathing.Pathfinder;
import xyz.oli.api.pathing.PathfinderResult;
import xyz.oli.api.pathing.PathfinderStrategy;
import xyz.oli.pathing.bstats.BStatsHandler;
import xyz.oli.pathing.model.pathing.PathImpl;
import xyz.oli.pathing.util.PathingScheduler;
import xyz.oli.api.wrapper.BukkitConverter;
import xyz.oli.api.wrapper.PathLocation;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

public class PathfinderImpl implements Pathfinder {
    
    private static final ForkJoinPool FORK_JOIN_POOL = new ForkJoinPool(Runtime.getRuntime().availableProcessors(), ForkJoinPool.defaultForkJoinWorkerThreadFactory, null, true);
    private static final LinkedHashSet<Location> EMPTY_LIST = new LinkedHashSet<>();
    
    private static final int MAX_CHECKS = 20000;
    
    private static final Vector[] OFFSETS = {
            
            new Vector(1, 0, 0),
            new Vector(-1, 0, 0),
            new Vector(0, 0, 1),
            new Vector(0, 0, -1),
            new Vector(0, 1, 0),
            new Vector(0, -1, 0),
    };
    
    @Override
    public PathfinderResult findPath(PathLocation start, PathLocation target, PathfinderStrategy strategy) {
        return seekPath(start, target, strategy);
    }
    
    @Override
    public CompletableFuture<PathfinderResult> findPathAsync(PathLocation start, PathLocation target, PathfinderStrategy strategy) {
        return CompletableFuture.supplyAsync(() -> seekPath(start, target, strategy), FORK_JOIN_POOL);
    }
    
    /* refactor */
    /* renamed for now since overlap with {@link #findPath} */
    private PathfinderResult seekPath(PathLocation start, PathLocation target, PathfinderStrategy strategy) {
    
        BStatsHandler.increasePathCount();
    
        PathingStartFindEvent startFindEvent = new PathingStartFindEvent(start, target, strategy);
        PathingScheduler.runOnMain(() -> Bukkit.getPluginManager().callEvent(startFindEvent));
    
        if (!start.getPathWorld().equals(target.getPathWorld()) || !strategy.endIsValid(start.getBlock()) || !strategy.endIsValid(target.getBlock()) || (startFindEvent.isCancelled())) {
            BStatsHandler.increaseFailedPathCount();
            return new PathfinderResultImpl(PathfinderResultImpl.PathfinderSuccess.INVALID, new PathImpl(BukkitConverter.toLocation(start), BukkitConverter.toLocation(target), EMPTY_LIST));
        }
    
        if (start.getBlockX() == target.getBlockX() && start.getBlockY() == target.getBlockY() && start.getBlockZ() == target.getBlockZ()) {
        
            LinkedHashSet<Location> nodeList = new LinkedHashSet<>();
            nodeList.add(BukkitConverter.toLocation(target));
        
            return new PathfinderResultImpl(PathfinderResultImpl.PathfinderSuccess.FOUND, new PathImpl(BukkitConverter.toLocation(start), BukkitConverter.toLocation(target), nodeList));
        }
    
        Node startNode = new Node(new PathLocation(start.getPathWorld(), start.getBlockX() + 0.5, start.getBlockY(), start.getBlockZ() + 0.5), start, target);
        Node targetNode = new Node(new PathLocation(target.getPathWorld(), target.getBlockX() + 0.5, target.getBlockY(), target.getBlockZ() + 0.5), start, target);
    
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<PathLocation> processed = new HashSet<>();
    
        queue.add(startNode);
    
        int depth = 0;
    
        while (!queue.isEmpty() && depth <= MAX_CHECKS) {
        
            Node node = queue.poll();
        
            depth++;
        
            if (node.equals(targetNode)) {
                return retracePath(startNode, node, start, target);
            }
        
            for (Node neighbourNode : getNeighbours(node, start, target)) {
            
                if (neighbourNode.equals(targetNode))
                    return retracePath(startNode, neighbourNode, start, target);
            
                if (!strategy.isValid(neighbourNode.getLocation().getBlock(),
                        node.getLocation().getBlock(),
                        node.getParent() == null ? node.getLocation().getBlock() : node.getParent().getLocation().getBlock()) || !processed.add(neighbourNode.getLocation()))
                    continue;
            
                queue.add(neighbourNode);
            }
        }
    
        BStatsHandler.increaseFailedPathCount();
    
        return new PathfinderResultImpl(PathfinderResultImpl.PathfinderSuccess.FAILED, new PathImpl(BukkitConverter.toLocation(start), BukkitConverter.toLocation(target), EMPTY_LIST));
    }
    
    private PathfinderResultImpl retracePath(Node startNode, Node endNode, PathLocation start, PathLocation target) {
        
        LinkedHashSet<Location> path = new LinkedHashSet<>();
        Node currentNode = endNode;
        
        while (!currentNode.equals(startNode)) {
            
            path.add(BukkitConverter.toLocation(currentNode.getLocation()));
            if (currentNode.getParent() == null) break;
            currentNode = currentNode.getParent();
        }
        
        List<Location> pathReversed = new ArrayList<>(path);
        Collections.reverse(pathReversed);
        
        PathingFinishedEvent pathingFinishedEvent = new PathingFinishedEvent(start, target, pathReversed);
        PathingScheduler.runOnMain(() -> Bukkit.getPluginManager().callEvent(pathingFinishedEvent));
        
        BStatsHandler.addLength(pathReversed.size());
        
        return new PathfinderResultImpl(PathfinderResultImpl.PathfinderSuccess.FOUND, new PathImpl(BukkitConverter.toLocation(start), BukkitConverter.toLocation(target), new LinkedHashSet<>(pathReversed)));
    }
    
    private Collection<Node> getNeighbours(Node node, PathLocation start, PathLocation target) {
        
        ArrayList<Node> neighbours = new ArrayList<>(OFFSETS.length);
        
        for (Vector offset : OFFSETS) {
            
            Vector midpoint = new Vector();
            
            if (offset.getY() != 0)
                midpoint.setX(offset.getX() / 2).setZ(offset.getZ() / 2);
            
            Node neighbourNode = new Node(node.getLocation().add(offset).add(midpoint), start, target);
            neighbourNode.setParent(node);
            
            neighbours.add(neighbourNode);
        }
        
        return neighbours;
    }
}
