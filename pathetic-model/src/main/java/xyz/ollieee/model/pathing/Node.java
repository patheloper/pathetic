package xyz.ollieee.model.pathing;

import xyz.ollieee.api.wrapper.PathLocation;
import xyz.ollieee.api.wrapper.PathVector;

import java.util.Objects;

public class Node implements Comparable<Node> {

    private final Integer depth;
    private final PathLocation location;
    private final PathLocation target;
    private final PathLocation start;
    
    private Node parent;

    Node(PathLocation location, PathLocation start, PathLocation target, Integer depth) {
        
        this.location = location;
        this.target = target;
        this.start = start;
        this.depth = depth;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    public Node getParent() {
        return this.parent;
    }
    
    public Integer getDepth() {
        return this.depth;
    }

    public double getCost() {
        return this.location.distance(this.target);
    }

    public PathLocation getStart() {
        return this.start;
    }

    public PathLocation getTarget() {
        return this.target;
    }

    public PathLocation getLocation() {
        return this.location.clone();
    }

    public boolean hasReachedEnd() {
        return this.location.getBlockX() == target.getBlockX() && this.location.getBlockY() == target.getBlockY() && this.location.getBlockZ() == target.getBlockZ();
    }

    private double getDistanceKey() {

        PathVector a = this.location.toVector();
        PathVector b = this.start.toVector();
        PathVector c = this.target.toVector();
        double v = a.subtract(b).getCrossProduct(c.subtract(b)).length() / c.subtract(b).length();

        return this.location.octileDistance(target) * (v*0.00002) + (0.01*this.target.distance(this.location));
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathLocation nodeLocation = ((Node) o).getLocation();
        return nodeLocation.getBlockX() == this.location.getBlockX() && nodeLocation.getBlockY() == this.location.getBlockY() && nodeLocation.getBlockZ() == this.location.getBlockZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.location);
    }

    @Override
    public int compareTo(Node o) {
        return (int) Math.signum(this.getDistanceKey() - o.getDistanceKey());
    }
}
