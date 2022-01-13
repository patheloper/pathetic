package xyz.oli.pathing.api.finder;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Objects;

public class Node implements Comparable<Node> {

    private Node parent;
    private final Location location;
    private final Location target;
    private final Location start;

    public Node(Location location, Location start, Location target) {
        this.location = location;
        this.target = target;
        this.start = start;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public boolean walkable() {
        return this.location.getBlock().getType() == Material.AIR;
    }

    public Node getParent() {
        return this.parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location nodeLocation = ((Node) o).getLocation();
        return nodeLocation.getBlockX() == this.location.getBlockX() && nodeLocation.getBlockY() == this.location.getBlockY() && nodeLocation.getBlockZ() == this.location.getBlockZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.location);
    }

    public double priorityKey() {
        return this.target.distance(this.location) + this.start.distance(this.location);
    }

    @Override
    public int compareTo(Node otherNode) {
        return (int) Math.signum(this.priorityKey() - otherNode.priorityKey());
    }
}
