package xyz.oli.pathing.model.path.finder;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import xyz.oli.pathing.model.path.Path;
import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;

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
     * @see Pathfinder#retracePath(Node, Node, Location, Location)
     */
    public PathfinderResult findPath(Location start, Location target, final int maxChecks, PathfinderStrategy strategy) {

        if (!start.getWorld().getName().equalsIgnoreCase(target.getWorld().getName()) || !strategy.verifyEnd(start) || !strategy.verifyEnd(target))
            return new PathfinderResult(PathfinderSuccess.FAILED, new Path(start, target, EMPTY_LIST));

        if (start.getBlockX() == target.getBlockX() && start.getBlockY() == target.getBlockY() && start.getBlockZ() == target.getBlockZ()){
    
            LinkedHashSet<Location> nodeList = new LinkedHashSet<>();
            nodeList.add(target);

            return new PathfinderResult(PathfinderSuccess.FOUND, new Path(start, target, nodeList));
        }

        Node startNode = new Node(new Location(start.getWorld(), start.getBlockX() + 0.5, start.getBlockY(), start.getBlockZ() + 0.5), start, target);
        Node targetNode = new Node(new Location(target.getWorld(), target.getBlockX() + 0.5, target.getBlockY(), target.getBlockZ() + 0.5), start, target);

        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<Node> processed = new HashSet<>();

        queue.add(startNode);

        int depth = 0;

        while (!queue.isEmpty()) {

            Node node = queue.poll();

            depth++;
            if (depth >= maxChecks) break;

            if (node.equals(targetNode)) {
                return retracePath(startNode, node, start, target);
            }

            for (Node neighbourNode : getNeighbours(node, start, target)) {

                if (!strategy.isValid(neighbourNode.getLocation(), node.getLocation(), node.getParent() == null ? node.getLocation() : node.getParent().getLocation()) || !processed.add(neighbourNode))
                    continue;

                queue.add(neighbourNode);
            }
        }

        return new PathfinderResult(PathfinderSuccess.FAILED, new Path(start, target, EMPTY_LIST));
    }

    public PathfinderResult findPath(Location start, Location end, PathfinderStrategy strategy) {
        return this.findPath(start, end, 20000, strategy);
    }

    private PathfinderResult retracePath(Node startNode, Node endNode, Location start, Location target) {
    
        LinkedHashSet<Location> path = new LinkedHashSet<>();
        Node currentNode = endNode;

        while (!currentNode.equals(startNode)) {

            path.add(currentNode.getLocation());
            if (currentNode.getParent() == null) break;
            currentNode = currentNode.getParent();
        }

        List<Location> pathReversed = new ArrayList<>(path);
        Collections.reverse(pathReversed);
        
        return new PathfinderResult(PathfinderSuccess.FOUND, new Path(start, target, new LinkedHashSet<>(path)));
    }

    private Collection<Node> getNeighbours(Node node, Location start, Location target) {

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
