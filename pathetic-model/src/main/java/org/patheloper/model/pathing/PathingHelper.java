package org.patheloper.model.pathing;

import lombok.NonNull;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.model.pathing.result.PathImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * TODO: 25.11.2022
 *  This class has a lot improvement potential.
 *
 * TODO: 14.12.2022
 *  This class is a mess. It needs to be refactored. I'm planning to merge it into the AbstractPathfinder class.
 *  I'm also planning to make it more generic, so that it can be used for other pathfinding algorithms. More or less with the same interface.
 *  F.e. could the method isNodeValid be custom implemented and evaluateNewNodes be force-used within the internals of the AbstractPathfinder.
 *  The AbstractPathfinder could also keep track of the latestNode and the latestPath.
 *      With that methods like getProgress would have more of a (simpler) internal use.
 */
class PathingHelper {

    static void evaluateNewNodes(Collection<Node> nodeQueue, Set<PathPosition> examinedPositions, Node currentNode, Offset offset, PathfinderStrategy strategy, SnapshotManager snapshotManager) {
        for (Node neighbourNode : fetchNeighbours(currentNode, offset.getVectors()))
            if (isNodeValid(neighbourNode, nodeQueue, snapshotManager, examinedPositions, strategy))
                nodeQueue.add(neighbourNode);
    }

    static int getProgress(Node node) {

        int length = 0;

        Node currentNode = node;
        while (currentNode != null) {
            length++;
            currentNode = currentNode.getParent();
        }

        return length;
    }

    static boolean isNodeValid(Node node, Collection<Node> nodeQueue, SnapshotManager snapshotManager, Set<PathPosition> examinedPositions, PathfinderStrategy strategy) {

        if (examinedPositions.contains(node.getPosition()))
            return false;

        if (nodeQueue.contains(node))
            return false;

        if (!isWithinWorldBounds(node.getPosition()))
            return false;

        if (!strategy.isValid(node.getPosition(), snapshotManager))
            return false;

        return examinedPositions.add(node.getPosition());
    }

    static boolean isWithinWorldBounds(PathPosition position) {
        return position.getPathEnvironment().getMinHeight() < position.getBlockY() && position.getBlockY() < position.getPathEnvironment().getMaxHeight();
    }

    static Collection<Node> fetchNeighbours(Node currentNode, PathVector[] offsets) {

        Set<Node> newNodes = new HashSet<>(offsets.length);

        for (PathVector offset : offsets) {

            Node newNode = new Node(currentNode.getPosition().add(offset), currentNode.getStart(), currentNode.getTarget(), currentNode.getDepth() + 1);
            newNode.setParent(currentNode);

            newNodes.add(newNode);
        }

        return newNodes;
    }

    static Path fetchRetracedPath(@NonNull Node node) {

        if(node.getParent() == null)
            return new PathImpl(node.getStart(), node.getTarget(), Collections.singletonList(node.getPosition()));

        List<PathPosition> path = new ArrayList<>();

        Node currentNode = node;
        while (currentNode != null) {
            path.add(currentNode.getPosition());
            currentNode = currentNode.getParent();
        }

        path.add(node.getStart());
        Collections.reverse(path);

        return new PathImpl(node.getStart(), node.getTarget(), path);
    }
}
