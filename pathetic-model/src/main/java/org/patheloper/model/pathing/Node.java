package org.patheloper.model.pathing;

import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;

import java.util.Objects;

public class Node implements Comparable<Node> {

    private final Integer depth;
    private final PathPosition position;
    private final PathPosition target;
    private final PathPosition start;
    
    private Node parent;

    public Node(PathPosition position, PathPosition start, PathPosition target, Integer depth) {
        
        this.position = position;
        this.target = target;
        this.start = start;
        this.depth = depth;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean hasReachedEnd() {
        return this.position.getBlockX() == target.getBlockX() && this.position.getBlockY() == target.getBlockY() && this.position.getBlockZ() == target.getBlockZ();
    }

    public double heuristic() {

        // The "v" is the perpendicular distance between the current position and the line from the start to the target
        PathVector a = this.position.toVector();
        PathVector b = this.start.toVector();
        PathVector c = this.target.toVector();
        double v = a.subtract(b).getCrossProduct(c.subtract(b)).length() / c.subtract(b).length();

        // We then multiply the perpendicular by the octile distance between the current position and the target
        // and the euclidean distance between the current position and the start
        return this.position.octileDistance(target) * (v*0.00002) + 0.01*this.target.distance(this.position);
    }

    public Node getParent() {
        return this.parent;
    }
    
    public Integer getDepth() {
        return this.depth;
    }

    public PathPosition getStart() {
        return this.start;
    }

    public PathPosition getTarget() {
        return this.target;
    }

    public PathPosition getPosition() {
        return this.position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathPosition nodePosition = ((Node) o).getPosition();
        return nodePosition.getBlockX() == this.position.getBlockX() && nodePosition.getBlockY() == this.position.getBlockY() && nodePosition.getBlockZ() == this.position.getBlockZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.position);
    }

    @Override
    public int compareTo(Node o) {
        // This is used in the priority queue to sort the nodes
        return (int) Math.signum(this.heuristic() - o.heuristic());
    }
}