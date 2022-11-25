package org.patheloper.model.pathing;

import lombok.NonNull;
import org.patheloper.api.event.PathingFinishedEvent;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathLocation;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.bukkit.event.EventPublisher;
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
 */
class PathingHelper {

    static void evaluateNewNodes(Collection<Node> nodeQueue, Set<PathLocation> examinedLocations, Node currentNode, Offset offset, PathfinderStrategy strategy, SnapshotManager snapshotManager) {
        for (Node neighbourNode : fetchNeighbours(currentNode, offset.getVectors()))
            if (isNodeValid(neighbourNode, nodeQueue, snapshotManager, examinedLocations, strategy))
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

    static boolean isNodeValid(Node node, Collection<Node> nodeQueue, SnapshotManager snapshotManager, Set<PathLocation> examinedLocations, PathfinderStrategy strategy) {

        if (examinedLocations.contains(node.getLocation()))
            return false;

        if (nodeQueue.contains(node))
            return false;

        if (!isWithinWorldBounds(node.getLocation()))
            return false;

        if (!strategy.isValid(node.getLocation(), snapshotManager))
            return false;

        return examinedLocations.add(node.getLocation());
    }

    static boolean isWithinWorldBounds(PathLocation location) {
        return location.getPathWorld().getMinHeight() < location.getBlockY() && location.getBlockY() < location.getPathWorld().getMaxHeight();
    }

    static Collection<Node> fetchNeighbours(Node currentNode, PathVector[] offsets) {

        Set<Node> newNodes = new HashSet<>(offsets.length);

        for (PathVector offset : offsets) {

            Node newNode = new Node(currentNode.getLocation().add(offset), currentNode.getStart(), currentNode.getTarget(), currentNode.getDepth() + 1);
            newNode.setParent(currentNode);

            newNodes.add(newNode);
        }

        return newNodes;
    }

    static Path fetchRetracedPath(@NonNull Node node) {

        if(node.getParent() == null)
            return new PathImpl(node.getStart(), node.getTarget(), Collections.singletonList(node.getLocation()));

        List<PathLocation> path = new ArrayList<>();

        Node currentNode = node;
        while (currentNode != null) {
            path.add(currentNode.getLocation());
            currentNode = currentNode.getParent();
        }

        path.add(node.getStart());
        Collections.reverse(path);

        return new PathImpl(node.getStart(), node.getTarget(), path);
    }

    static PathfinderResult finishPathing(PathfinderResult pathfinderResult) {
        EventPublisher.raiseEvent(new PathingFinishedEvent(pathfinderResult));
        return pathfinderResult;
    }

    /*
     * Bloating up like a bubble until a reachable block is found
     * The block itself might not be passable, but at least reachable from the outside
     *
     * NOTE: The reachable block is not guaranteed to be the closest reachable block
     */
    static PathBlock bubbleSearchAlternative(PathLocation target, Offset offset, SnapshotManager snapshotManager) {

        Set<PathLocation> newLocations = new HashSet<>();
        newLocations.add(target);

        Set<PathLocation> examinedLocations = new HashSet<>();
        while (!newLocations.isEmpty()) {

            Set<PathLocation> nextLocations = new HashSet<>();
            for (PathLocation location : newLocations) {

                for (PathVector vector : offset.getVectors()) {

                    PathLocation offsetLocation = location.add(vector);
                    PathBlock pathBlock = snapshotManager.getBlock(offsetLocation);

                    if (pathBlock.isPassable() && !pathBlock.getPathLocation().isInSameBlock(target))
                        return pathBlock;

                    if (!examinedLocations.contains(offsetLocation))
                        nextLocations.add(offsetLocation);
                }

                examinedLocations.add(location);
            }

            newLocations = nextLocations;
        }

        return snapshotManager.getBlock(target);
    }

}
