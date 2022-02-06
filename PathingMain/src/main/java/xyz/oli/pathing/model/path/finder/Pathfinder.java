package xyz.oli.pathing.model.path.finder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import xyz.oli.event.PathingFinishedEvent;
import xyz.oli.event.PathingStartFindEvent;
import xyz.oli.pathing.PathfinderStrategy;
import xyz.oli.pathing.model.path.Path;
import xyz.oli.pathing.util.PathingScheduler;
import xyz.oli.wrapper.BukkitConverter;
import xyz.oli.wrapper.PathLocation;

import java.util.*;

public class Pathfinder {

    private static final LinkedHashSet<Location> EMPTY_LIST = new LinkedHashSet<>();

    private final Vector[] offsets = {

            new Vector(1,0,0),
            new Vector(-1,0,0),
            new Vector(0,0,1),
            new Vector(0,0,-1),
            new Vector(0,1,0),
            new Vector(0,-1,0),
    };

    /**
     * Uses the A* algorithm to find a path to the {@param target} from the {@param start}.
     *
     * Uses the Node's parent Node in order to then retrace its steps once the end is reached.
     * @see Pathfinder#retracePath(Node, Node, PathLocation, PathLocation)
     */
    public PathfinderResult findPath(PathLocation start, PathLocation target, final int maxChecks, PathfinderStrategy strategy) {

        PathingStartFindEvent startFindEvent = new PathingStartFindEvent(start, target, strategy);
        PathingScheduler.runOnMain(() -> Bukkit.getPluginManager().callEvent(startFindEvent));

        if (!start.getPathWorld().equals(target.getPathWorld()) || !strategy.endIsValid(start.getBlock()) || !strategy.endIsValid(target.getBlock()) || (startFindEvent.isCancelled()))
            return new PathfinderResult(PathfinderResult.PathfinderSuccess.INVALID, new Path(BukkitConverter.toLocation(start), BukkitConverter.toLocation(target), EMPTY_LIST));

        if (start.getBlockX() == target.getBlockX() && start.getBlockY() == target.getBlockY() && start.getBlockZ() == target.getBlockZ()){
    
            LinkedHashSet<Location> nodeList = new LinkedHashSet<>();
            nodeList.add(BukkitConverter.toLocation(target));

            return new PathfinderResult(PathfinderResult.PathfinderSuccess.FOUND, new Path(BukkitConverter.toLocation(start), BukkitConverter.toLocation(target), nodeList));
        }

        Node startNode = new Node(new PathLocation(start.getPathWorld(), start.getBlockX() + 0.5, start.getBlockY(), start.getBlockZ() + 0.5), start, target);
        Node targetNode = new Node(new PathLocation(target.getPathWorld(), target.getBlockX() + 0.5, target.getBlockY(), target.getBlockZ() + 0.5), start, target);

        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<PathLocation> processed = new HashSet<>();

        queue.add(startNode);

        int depth = 0;

        while (!queue.isEmpty() && depth <= maxChecks) {

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

        return new PathfinderResult(PathfinderResult.PathfinderSuccess.FAILED, new Path(BukkitConverter.toLocation(start), BukkitConverter.toLocation(target), EMPTY_LIST));
    }

    public PathfinderResult findPath(PathLocation start, PathLocation end, PathfinderStrategy strategy) {
        return this.findPath(start, end, 20000, strategy);
    }

    private PathfinderResult retracePath(Node startNode, Node endNode, PathLocation start, PathLocation target) {
    
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
        
        return new PathfinderResult(PathfinderResult.PathfinderSuccess.FOUND, new Path(BukkitConverter.toLocation(start), BukkitConverter.toLocation(target), new LinkedHashSet<>(pathReversed)));
    }

    private Collection<Node> getNeighbours(Node node, PathLocation start, PathLocation target) {

        ArrayList<Node> neighbours = new ArrayList<>(this.offsets.length);

        for (Vector offset : this.offsets) {

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
