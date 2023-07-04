package org.patheloper.model.pathing;

import lombok.EqualsAndHashCode;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Node implements Comparable<Node> {

    private final Integer depth;

    @EqualsAndHashCode.Include
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

    public boolean isAtTarget() {
        return this.position.getBlockX() == target.getBlockX() && this.position.getBlockY() == target.getBlockY() && this.position.getBlockZ() == target.getBlockZ();
    }

    public double heuristic() {
        double v = calculatePerpendicularDistance();
        return this.position.octileDistance(target) * (v*0.00002) + 0.01*this.target.distance(this.position);
    }

    private double calculatePerpendicularDistance() {
        PathVector a = this.position.toVector();
        PathVector b = this.start.toVector();
        PathVector c = this.target.toVector();
        return a.subtract(b).getCrossProduct(c.subtract(b)).length() / c.subtract(b).length();
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
    public int compareTo(Node o) {
        // This is used in the priority queue to sort the nodes
        return (int) Math.signum(this.heuristic() - o.heuristic());
    }
}
