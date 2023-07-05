package org.patheloper.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
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
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * This is a utility class that provides various helper methods for working with {@link Node} objects.
 * These methods do not fit into any other class and are provided here for convenience.
 */
@UtilityClass
public class NodeUtil {

    private static boolean debugMode = false;
    private static Cache<Node, Color> evaluatedNodesCache;

    /**
     * Enables or disables debugging mode.
     *
     * @param enabled true to enable debugging mode, false to disable it
     */
    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
        if (enabled) {
            evaluatedNodesCache = CacheBuilder.newBuilder()
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .build();
        } else {
            evaluatedNodesCache = null;
        }
    }

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
     * Spawns particles continuously using a 1 tick spigot scheduler.
     * The particle color is based on the depth of the nodes.
     */
    public static void spawnParticlesContinuously(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (debugMode) {
                for (Map.Entry<Node, Color> entry : evaluatedNodesCache.asMap().entrySet()) {
                    Node evaluatedNode = entry.getKey();
                    PathPosition position = evaluatedNode.getPosition();

                    Location location = new Location(Bukkit.getWorld(position.getPathEnvironment().getUuid()), position.getX(), position.getY(), position.getZ());
                    location.add(0.5, 0.5, 0.5);
                    Particle.DustOptions dustOptions = new Particle.DustOptions(entry.getValue(), 1.0f);
                    location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0, dustOptions);
                }
            }
        }, 0L, 1L);
    }

    /**
     * Generates a hex color based on the depth of the node.
     * The hex value will be darker the more depth the node has.
     *
     * @param depth the depth of the node
     * @return the generated hex color
     */
    private static Color generateHexColor(int depth) {
        int red = Math.max(0, 255 - depth * 10);
        int green = Math.max(0, 255 - depth * 10);
        int blue = Math.max(0, 255 - depth * 10);

        return Color.fromRGB(red, green, blue);
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

    private static final Set<Node> notValidCornerCutsSet = new HashSet<>();

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
    public static boolean isNodeValid(Node node, Node currentNode, Collection<Node> nodeQueue,
                                      SnapshotManager snapshotManager, Set<PathPosition> examinedPositions,
                                      PathfinderStrategy strategy, PathVector[] cornerCuts) {
        if (examinedPositions.contains(node.getPosition()) || nodeQueue.contains(node) ||
                !isWithinWorldBounds(node.getPosition()) || !strategy.isValid(node.getPosition(), snapshotManager) || notValidCornerCutsSet.contains(node)) {
            notValidCornerCutsSet.add(node);
            return false;
        }

        for (PathVector cornerCut : cornerCuts) {
            if (isWithinWorldBounds(currentNode.getPosition().add(cornerCut)) &&
                    strategy.isValid(currentNode.getPosition().add(cornerCut), snapshotManager) &&
                    !notValidCornerCutsSet.contains(node)) {
                return examinedPositions.add(node.getPosition());
            }
            notValidCornerCutsSet.add(node);
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

            if (debugMode) {
                Color hexColor = generateHexColor(newNode.getDepth());
                evaluatedNodesCache.put(newNode, hexColor);
            }

            if (isNodeValid(newNode, currentNode, nodeQueue, snapshotManager, examinedPositions, strategy, entry.getCornerCuts()))
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
