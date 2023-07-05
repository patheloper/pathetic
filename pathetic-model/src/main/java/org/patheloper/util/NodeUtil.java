package org.patheloper.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathBlock;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.model.pathing.Node;
import org.patheloper.model.pathing.Offset;
import org.patheloper.model.pathing.result.PathImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is a utility class that provides various helper methods for working with {@link Node} objects.
 * These methods do not fit into any other class and are provided here for convenience.
 */
@UtilityClass
public class NodeUtil {

    /**
     * Evaluates new nodes and adds them to the given node queue if they are valid.
     *
     * @param nodeQueue         the node queue to add new nodes to
     * @param examinedPositions a set of examined positions
     * @param currentNode       the current node
     * @param offset            the offset to apply to the current node's position to find neighbours
     * @param strategy          the pathfinder strategy to use for validating nodes
     * @param snapshotManager   the snapshot manager to use for validating nodes
     */
    public static void evaluateNewNodes(Collection<Node> nodeQueue, Set<PathPosition> examinedPositions, Node currentNode, Offset offset, PathfinderStrategy strategy, SnapshotManager snapshotManager) {
        nodeQueue.addAll(fetchValidNeighbours(nodeQueue, examinedPositions, currentNode, offset, strategy, snapshotManager));
    }

    /**
     * Bloating up like a bubble until a reachable block is found
     * The block itself might not be passable, but at least reachable from the outside
     *
     * @api.Note The reachable block is not guaranteed to be the closest reachable block
     */
    public static PathBlock bubbleSearchAlternative(PathPosition target, Offset offset, SnapshotManager snapshotManager) {
        Set<PathPosition> newPositions = new HashSet<>();
        newPositions.add(target);

        Set<PathPosition> examinedPositions = new HashSet<>();
        while (!newPositions.isEmpty()) {
            Set<PathPosition> nextPositions = new HashSet<>();
            PathBlock pathBlock = getPathBlock(target, offset, snapshotManager, newPositions, examinedPositions, nextPositions);
            if (pathBlock != null) return pathBlock;
            newPositions = nextPositions;
        }

        return snapshotManager.getBlock(target);
    }

    /**
     * Determines whether the given node is valid and can be added to the node queue.
     *
     * @param node              the node to validate
     * @param nodeQueue         the node queue to check for duplicates
     * @param snapshotManager   the snapshot manager to use for validating nodes
     * @param examinedPositions a set of examined positions
     * @param strategy          the pathfinder strategy to use for validating nodes
     * @return {@code true} if the node is valid and can be added to the node queue, {@code false} otherwise
     */
    public static boolean isNodeValid(Node node, Collection<Node> nodeQueue, SnapshotManager snapshotManager, Set<PathPosition> examinedPositions, PathfinderStrategy strategy, PathVector[] cornerCuts) {
        if (examinedPositions.contains(node.getPosition()) || nodeQueue.contains(node) || !isWithinWorldBounds(node.getPosition()) || !strategy.isValid(node.getPosition(), snapshotManager))
            return false;

        // The idea here is that if ANY of the corner cuts are valid, then the node is valid.
        // Doesn't seem to work at all though...
        // TODO: Fix this
        for (PathVector cornerCut : cornerCuts) {
            if (isNodeValid(createNeighbourNode(node, cornerCut), nodeQueue, snapshotManager, examinedPositions, strategy, new PathVector[0]))
                return examinedPositions.add(node.getPosition());
        }

        return examinedPositions.add(node.getPosition());
    }

    /**
     * Determines whether the given position is within the bounds of the world.
     *
     * @param position the position to check
     * @return {@code true} if the position is within the bounds of the world, {@code false} otherwise
     */
    public static boolean isWithinWorldBounds(PathPosition position) {
        return position.getPathEnvironment().getMinHeight() < position.getBlockY() && position.getBlockY() < position.getPathEnvironment().getMaxHeight();
    }

    /**
     * Fetches the neighbours of the given node.
     *
     * @param currentNode the node to fetch neighbours for
     * @param offset      the offset to apply to the current node's position to find neighbours
     * @return a collection of neighbour nodes
     */
    public static Collection<Node> fetchValidNeighbours(Collection<Node> nodeQueue, Set<PathPosition> examinedPositions, Node currentNode, Offset offset, PathfinderStrategy strategy, SnapshotManager snapshotManager) {
        Set<Node> newNodes = new HashSet<>(offset.getOffsets().length);

        for (Offset.OffsetEntry entry : offset.getOffsets()) {
            Node newNode = createNeighbourNode(currentNode, entry.getVector());

            if (isNodeValid(newNode, nodeQueue, snapshotManager, examinedPositions, strategy, entry.getCornerCuts()))
                newNodes.add(newNode);
        }

        return newNodes;
    }

    private static Node createNeighbourNode(Node currentNode, PathVector offset) {
        Node newNode = new Node(currentNode.getPosition().add(offset), currentNode.getStart(), currentNode.getTarget(), currentNode.getDepth() + 1);
        newNode.setParent(currentNode);
        return newNode;
    }

    public static Path fetchMergedPath(Node endNode1, Node endNode2) {
        List<PathPosition> path1 = tracePathFromNode(endNode1);
        List<PathPosition> path2 = tracePathFromNode(endNode2);

        List<PathPosition> mergedPath = combinePaths(path1, path2);
        removeOverlappingNodes(mergedPath);

        return new PathImpl(endNode1.getStart(), endNode1.getTarget(), mergedPath);
    }

    /**
     * Fetches the path represented by the given node by retracing the steps from the node's parent.
     *
     * @param node the node to fetch the path for
     * @return the path represented by the given node
     */
    public static Path fetchRetracedPath(@NonNull Node node) {

        if(node.getParent() == null)
            return new PathImpl(node.getStart(), node.getTarget(), Collections.singletonList(node.getPosition()));

        List<PathPosition> path = tracePathFromNode(node);
        return new PathImpl(node.getStart(), node.getTarget(), path);
    }

    private static List<PathPosition> tracePathFromNode(Node endNode) {
        List<PathPosition> path = new ArrayList<>();
        Node currentNode = endNode;

        while (currentNode != null) {
            path.add(currentNode.getPosition());
            currentNode = currentNode.getParent();
        }

        Collections.reverse(path); // make it the right order
        return path;
    }

    private static List<PathPosition> combinePaths(List<PathPosition> path1, List<PathPosition> path2) {
        List<PathPosition> mergedPath = new ArrayList<>(path1);
        mergedPath.addAll(path2);
        return mergedPath;
    }

    private static void removeOverlappingNodes(List<PathPosition> mergedPath) {
        for (int i = 0; i < mergedPath.size() - 1; i++) {
            if (mergedPath.get(i).equals(mergedPath.get(i + 1))) {
                mergedPath.remove(i + 1);
                i--;
            }
        }
    }

    private static PathBlock getPathBlock(PathPosition target, Offset offset, SnapshotManager snapshotManager, Set<PathPosition> newPositions, Set<PathPosition> examinedPositions, Set<PathPosition> nextPositions) {
        for (PathPosition position : newPositions) {
            for (Offset.OffsetEntry entry : offset.getOffsets()) {

                PathPosition offsetPosition = position.add(entry.getVector());
                PathBlock pathBlock = snapshotManager.getBlock(offsetPosition);

                if (pathBlock.isPassable() && !pathBlock.getPathPosition().isInSameBlock(target))
                    return pathBlock;

                if (!examinedPositions.contains(offsetPosition))
                    nextPositions.add(offsetPosition);
            }
            examinedPositions.add(position);
        }
        return null;
    }
}
