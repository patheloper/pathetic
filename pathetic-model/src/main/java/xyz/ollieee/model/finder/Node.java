package xyz.ollieee.model.finder;

import xyz.ollieee.api.wrapper.PathLocation;

import java.util.Objects;

public class Node implements Comparable<Node>{

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

    double getDistanceKey() {

        double heuristic = this.location.octileDistance(target);

        double dx1 = this.location.getX() - this.target.getX();
        double dy1 = this.location.getY() - this.target.getY();
        double dx2 = this.start.getX() - this.target.getX();
        double dy2 = this.start.getY() - this.target.getY();
        double cross = Math.abs(dx1*dy2 - dx2*dy1);
        heuristic += cross*0.0005;

        return heuristic;
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
