package xyz.oli.pathing.api.finder;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import xyz.oli.pathing.api.Path;

import java.util.*;

public class PathFinder {

    private static final LinkedList<Location> EMPTY_LIST = new LinkedList<>();

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
     * @see PathFinder#retracePath(Node, Node, Location, Location)
     */
    public PathResult findPath(Location start, Location target, final int maxChecks) {

        if (!start.getWorld().getName().equalsIgnoreCase(target.getWorld().getName()))
            return new PathResult(PathSuccess.FAILED, new Path(start, target, EMPTY_LIST));

        if (start.getBlockX() == target.getBlockX() && start.getBlockY() == target.getBlockY() && start.getBlockZ() == target.getBlockZ()){

            LinkedList<Location> nodeList = new LinkedList<>();
            nodeList.add(target);

            return new PathResult(PathSuccess.FOUND, new Path(start, target, nodeList));
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

                if (!neighbourNode.walkable() || !processed.add(neighbourNode))
                    continue;

                queue.add(neighbourNode);
            }
        }

        return new PathResult(PathSuccess.FAILED, new Path(start, target, EMPTY_LIST));
    }

    public PathResult findPath(Location start, Location end) {
        return this.findPath(start, end, 20000);
    }

    private PathResult retracePath(Node startNode, Node endNode, Location start, Location target) {

        LinkedList<Location> path = new LinkedList<>();
        Node currentNode = endNode;

        while (!currentNode.equals(startNode)) {

            path.add(currentNode.getLocation());
            if (currentNode.getParent() == null) break;
            currentNode = currentNode.getParent();
        }

        Collections.reverse(path);
        return new PathResult(PathSuccess.FOUND, new Path(start, target, path));
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
