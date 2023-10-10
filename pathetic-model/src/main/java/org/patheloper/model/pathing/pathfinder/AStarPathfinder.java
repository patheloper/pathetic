package org.patheloper.model.pathing.pathfinder;

import lombok.NonNull;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.snapshot.SnapshotManager;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.api.wrapper.PathVector;
import org.patheloper.model.pathing.Node;
import org.patheloper.model.pathing.Offset;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.util.ErrorLogger;
import org.patheloper.util.WatchdogUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A pathfinder that uses the A* algorithm.
 */
public class AStarPathfinder extends AbstractPathfinder {

    public AStarPathfinder(PathingRuleSet pathingRuleSet) {
        super(pathingRuleSet);
    }
    
    @Override
    protected PathfinderResult resolvePath(PathPosition start, PathPosition target, PathfinderStrategy strategy) {
        Node startNode = new Node(start.floor(), start.floor(), target.floor(), 0);
        
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));
        Set<PathPosition> examinedPositions = new HashSet<>();
        
        int depth = 1;
        Node fallbackNode = startNode;
        
        while (!nodeQueue.isEmpty() && depth <= pathingRuleSet.getMaxIterations()) {
            tickWatchdogIfNeeded(depth);
            
            Node currentNode = getNextNodeFromQueue(nodeQueue);
            fallbackNode = currentNode;
            
            if (hasReachedLengthLimit(currentNode)) {
                return finishPathing(PathState.LENGTH_LIMITED, currentNode);
            }
            
            if (currentNode.isTarget()) {
                return finishPathing(PathState.FOUND, currentNode);
            }
            
            evaluateNewNodes(nodeQueue,
                    examinedPositions,
                    currentNode,
                    offset,
                    strategy,
                    snapshotManager,
                    this.pathingRuleSet.isAllowingDiagonal());
            depth++;
        }
        
        return backupPathfindingOrFailure(depth, start, target, strategy, fallbackNode);
    }
    
    private void tickWatchdogIfNeeded(int depth) {
        if (depth % 500 == 0) {
            WatchdogUtil.tickWatchdog();
        }
    }
    
    private Node getNextNodeFromQueue(PriorityQueue<Node> nodeQueue) {
        Node currentNode = nodeQueue.poll();
        if (currentNode == null) {
            throw ErrorLogger.logFatalError("A node was null when it shouldn't have been");
        }
        return currentNode;
    }
    
    private boolean hasReachedLengthLimit(Node currentNode) {
        return pathingRuleSet.getMaxLength() != 0 && currentNode.getDepth() > pathingRuleSet.getMaxLength();
    }
    
    private PathfinderResult finishPathing(PathState pathState, Node currentNode) {
        return finishPathing(new PathfinderResultImpl(pathState, fetchRetracedPath(currentNode)));
    }
    
    /**
     * If the pathfinder has failed to find a path, it will try to still give a result.
     */
    private PathfinderResult backupPathfindingOrFailure(int depth, PathPosition start, PathPosition target, PathfinderStrategy strategy, Node fallbackNode) {
        return maxIterations(depth, fallbackNode)
                .or(() -> counterCheck(start, target, strategy))
                .or(() -> fallback(fallbackNode))
                .orElse(finishPathing(new PathfinderResultImpl(
                        PathState.FAILED,
                        new PathImpl(start, target, EMPTY_LINKED_HASHSET))));
    }
    
    /**
     * Checks if the pathfinder has reached the maximum number of iterations.
     */
    private Optional<PathfinderResult> maxIterations(int depth, Node fallbackNode) {
        if(depth > pathingRuleSet.getMaxIterations())
            return Optional.of(finishPathing(new PathfinderResultImpl(
                    PathState.MAX_ITERATIONS_REACHED,
                    fetchRetracedPath(fallbackNode))));
        return Optional.empty();
    }
    
    /**
     * Checks if the pathfinder is allowed to fallback to the last node it examined and retraces the path from there.
     */
    private Optional<PathfinderResult> fallback(Node fallbackNode) {
        if (pathingRuleSet.isAllowingFallback())
            return Optional.of(finishPathing(new PathfinderResultImpl(
                    PathState.FALLBACK,
                    fetchRetracedPath(fallbackNode))));
        return Optional.empty();
    }
    
    /**
     * Checks if the pathfinder can find a path from the target to the start.
     */
    private Optional<PathfinderResult> counterCheck(PathPosition start, PathPosition target, PathfinderStrategy strategy) {
        if(!pathingRuleSet.isCounterCheck()) return Optional.empty();
        
        AStarPathfinder aStarPathfinder = new AStarPathfinder(pathingRuleSet.withCounterCheck(false));
        PathfinderResult pathfinderResult = aStarPathfinder.resolvePath(target, start, strategy);
        
        if(pathfinderResult.getPathState() == PathState.FOUND)
            return Optional.of(pathfinderResult);
        
        return Optional.empty();
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
    private void evaluateNewNodes(Collection<Node> nodeQueue,
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
    private boolean isNodeValid(Node currentNode,
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
            if (!isCornerCutInvalid(cuttingNode, snapshotManager, strategy))
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
    private boolean isWithinWorldBounds(PathPosition position) {
        return position.getPathEnvironment().getMinHeight() < position.getBlockY()
                && position.getBlockY() < position.getPathEnvironment().getMaxHeight();
    }
    
    /**
     * Fetches the neighbours of the given node.
     *
     * @param currentNode      the node to fetch neighbours for
     * @param offset           the offset to apply to the current node's position to find neighbours
     * @param allowingDiagonal
     * @return a collection of neighbour nodes
     */
    private Collection<Node> fetchValidNeighbours(Collection<Node> nodeQueue,
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
                newNodes.add(newNode);
            }
        }
        
        return newNodes;
    }
    
    /**
     * Fetches the path represented by the given node by retracing the steps from the node's parent.
     *
     * @param node the node to fetch the path for
     * @return the path represented by the given node
     */
    private Path fetchRetracedPath(@NonNull Node node) {
        
        if(node.getParent() == null)
            return new PathImpl(node.getStart(), node.getTarget(), Collections.singletonList(node.getPosition()));
        
        List<PathPosition> path = tracePathFromNode(node);
        return new PathImpl(node.getStart(), node.getTarget(), path);
    }
    
    /**
     * Creates a new node based on the given node and offset.
     */
    private Node createNeighbourNode(Node currentNode, PathVector offset) {
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
    private boolean isNodeInvalid(Node node,
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
     * This method checks if the given corner cut is valid. Here we explicitly don't check for
     * the already examined positions and the node queue
     * like in {@link #isNodeInvalid(Node, Collection, SnapshotManager, Set, PathfinderStrategy)}, because
     * this is not a node which is going to end in the node queue.
     */
    private boolean isCornerCutInvalid(Node cut,
                                              SnapshotManager snapshotManager,
                                              PathfinderStrategy strategy) {
        return !isWithinWorldBounds(cut.getPosition())
                || !strategy.isValid(cut.getPosition(), snapshotManager);
    }
    
    /**
     * Traces the path from the given node by retracing the steps from the node's parent.
     */
    private List<PathPosition> tracePathFromNode(Node endNode) {
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
