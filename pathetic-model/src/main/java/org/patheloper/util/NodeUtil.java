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
import org.bukkit.scheduler.BukkitRunnable;
import org.patheloper.Pathetic;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
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
            spawnParticlesContinuously(Pathetic.getPluginInstance());
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
     * @param allowingDiagonal  whether diagonal movement is allowed
     */
    public static void evaluateNewNodes(Collection<Node> nodeQueue,
                                        Set<PathPosition> examinedPositions,
                                        Node currentNode,
                                        Offset offset,
                                        PathfinderStrategy strategy,
                                        SnapshotManager snapshotManager,
                                        boolean allowingDiagonal) {
        nodeQueue.addAll(fetchValidNeighbours(nodeQueue,
                examinedPositions,
                currentNode,
                offset,
                strategy,
                snapshotManager,
                allowingDiagonal));
    }

    /**
     * Determines whether the given node is valid and can be added to the node queue.
     *
     * @param currentNode       the node we are moving from
     * @param newNode           the node to validate
     * @param nodeQueue         the node queue to check for duplicates
     * @param snapshotManager   the snapshot manager to use for validating nodes
     * @param examinedPositions a set of examined positions
     * @param strategy          the pathfinder strategy to use for validating nodes
     * @param allowingDiagonal
     * @return {@code true} if the node is valid and can be added to the node queue, {@code false} otherwise
     */
    public static boolean isNodeValid(Node currentNode,
                                      Node newNode,
                                      Collection<Node> nodeQueue,
                                      SnapshotManager snapshotManager,
                                      Set<PathPosition> examinedPositions,
                                      PathfinderStrategy strategy,
                                      PathVector[] cornerCuts,
                                      boolean allowingDiagonal) {
        if (isNodeInvalid(newNode, nodeQueue, snapshotManager, examinedPositions, strategy))
            return false;

        /*
         * So at this point there is nothing wrong with the node itself, We can move to it technically.
         * But we need to check if we can move to it from the current node. If we are moving diagonally, we need to check
         * if we can move to the adjacent nodes as well. The cornerCuts represent the offsets from the current node to the
         * adjacent nodes. If we can't move to any of the adjacent nodes, we can't move to the current node either.
         */

        if (!allowingDiagonal)
            return examinedPositions.add(newNode.getPosition());

        // If there are no corner cuts, we can move to the new node because we are not moving diagonally.
        if(cornerCuts.length == 0)
            return examinedPositions.add(newNode.getPosition());

        for (PathVector cornerCut : cornerCuts) {
            // Let's create the neighbour node and check if we can move to it
            Node cuttingNode = createNeighbourNode(currentNode, cornerCut);
            // If it's not invalid, we can move to the corner cut which means we can move to the new node
            if (!isNodeInvalid(cuttingNode, nodeQueue, snapshotManager, examinedPositions, strategy))
                // We can move to the corner cut, so its valid so we add the new node to the examined positions
                return examinedPositions.add(newNode.getPosition());
        }

        // None of the corner cuts are valid, so we can't move to the new node
        return false;
    }

    /**
     * Determines whether the given position is within the bounds of the world.
     *
     * @param position the position to check
     * @return {@code true} if the position is within the bounds of the world, {@code false} otherwise
     */
    public static boolean isWithinWorldBounds(PathPosition position) {
        return position.getPathEnvironment().getMinHeight() < position.getBlockY()
                && position.getBlockY() < position.getPathEnvironment().getMaxHeight();
    }
    
    private static final Color DARK_RED_HEX_COLOR = Color.fromRGB(0x8B0000);
    private static final Color DARK_GREEN_HEX_COLOR = Color.fromRGB(0x006400);

    /**
     * Fetches the neighbours of the given node.
     *
     * @param currentNode      the node to fetch neighbours for
     * @param offset           the offset to apply to the current node's position to find neighbours
     * @param allowingDiagonal
     * @return a collection of neighbour nodes
     */
    public static Collection<Node> fetchValidNeighbours(Collection<Node> nodeQueue,
                                                        Set<PathPosition> examinedPositions,
                                                        Node currentNode,
                                                        Offset offset,
                                                        PathfinderStrategy strategy,
                                                        SnapshotManager snapshotManager,
                                                        boolean allowingDiagonal) {
        Set<Node> newNodes = new HashSet<>(offset.getEntries().length);

        for (Offset.OffsetEntry entry : offset.getEntries()) {
            Node newNode = createNeighbourNode(currentNode, entry.getVector());

            if (isNodeValid(currentNode, newNode,
                    nodeQueue,
                    snapshotManager,
                    examinedPositions,
                    strategy,
                    entry.getCornerCuts(),
                    allowingDiagonal)) {
                debug(newNode, DARK_GREEN_HEX_COLOR);
                newNodes.add(newNode);
            } else {
                debug(newNode, DARK_RED_HEX_COLOR);
            }
        }

        return newNodes;
    }
    
    private static void debug(Node node, Color color) {
        if(debugMode) {
            evaluatedNodesCache.put(node, color);
        }
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

    /**
     * Creates a new node based on the given node and offset.
     */
    private static Node createNeighbourNode(Node currentNode, PathVector offset) {
        Node newNode = new Node(currentNode.getPosition().add(offset),
                currentNode.getStart(),
                currentNode.getTarget(),
                currentNode.getDepth() + 1);
        newNode.setParent(currentNode);
        return newNode;
    }

    /**
     * @return whether the given node is invalid or not
     */
    private static boolean isNodeInvalid(Node node,
                                         Collection<Node> nodeQueue,
                                         SnapshotManager snapshotManager,
                                         Set<PathPosition> examinedPositions,
                                         PathfinderStrategy strategy) {
        return examinedPositions.contains(node.getPosition())
                || nodeQueue.contains(node)
                || !isWithinWorldBounds(node.getPosition())
                || !strategy.isValid(node.getPosition(), snapshotManager);
    }

    /**
     * Spawns particles continuously using a 1 tick spigot scheduler.
     * The particle color is based on the depth of the nodes.
     */
    private static void spawnParticlesContinuously(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (debugMode) {
                    for (Map.Entry<Node, Color> entry : evaluatedNodesCache.asMap().entrySet()) {
                        Node evaluatedNode = entry.getKey();
                        PathPosition position = evaluatedNode.getPosition();

                        Location location = new Location(Bukkit.getWorld(
                                position.getPathEnvironment().getUuid()),
                                position.getX(),
                                position.getY(),
                                position.getZ());
                        location.add(0.5, 0.5, 0.5);

                        Particle.DustOptions dustOptions = new Particle.DustOptions(entry.getValue(), 1.0f);
                        location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0, dustOptions);
                    }
                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /**
     * Traces the path from the given node by retracing the steps from the node's parent.
     */
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
}
