package org.patheloper.model.pathing.pathfinder;

import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.model.pathing.Node;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.util.ErrorLogger;
import org.patheloper.util.NodeUtil;
import org.patheloper.util.WatchdogUtil;

import java.util.Collections;
import java.util.HashSet;
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
            
            if (currentNode.isAtTarget()) {
                return finishPathing(PathState.FOUND, currentNode);
            }
            
            evaluateNewNodes(nodeQueue, strategy, examinedPositions, currentNode);
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
        return finishPathing(new PathfinderResultImpl(pathState, NodeUtil.fetchRetracedPath(currentNode)));
    }
    
    private void evaluateNewNodes(PriorityQueue<Node> nodeQueue,
                                  PathfinderStrategy strategy,
                                  Set<PathPosition> examinedPositions,
                                  Node currentNode) {
        NodeUtil.evaluateNewNodes(nodeQueue, examinedPositions, currentNode, offset, strategy, snapshotManager, this.pathingRuleSet.isAllowingDiagonal());
    }

    private PathfinderResult backupPathfindingOrFailure(int depth, PathPosition start, PathPosition target, PathfinderStrategy strategy, Node fallbackNode) {
        return maxIterations(depth, fallbackNode)
                .or(() -> counterCheck(start, target, strategy))
                .or(() -> fallback(fallbackNode))
                .orElse(finishPathing(new PathfinderResultImpl(
                        PathState.FAILED,
                        new PathImpl(start, target, EMPTY_LINKED_HASHSET))));
    }
    
    private Optional<PathfinderResult> maxIterations(int depth, Node fallbackNode) {
        if(depth > pathingRuleSet.getMaxIterations())
            return Optional.of(finishPathing(new PathfinderResultImpl(
                    PathState.MAX_ITERATIONS_REACHED,
                    NodeUtil.fetchRetracedPath(fallbackNode))));
        return Optional.empty();
    }
    
    private Optional<PathfinderResult> fallback(Node fallbackNode) {
        if (pathingRuleSet.isAllowingFallback())
            return Optional.of(finishPathing(new PathfinderResultImpl(
                    PathState.FALLBACK,
                    NodeUtil.fetchRetracedPath(fallbackNode))));
        return Optional.empty();
    }
    
    private Optional<PathfinderResult> counterCheck(PathPosition start, PathPosition target, PathfinderStrategy strategy) {
        if(!pathingRuleSet.isCounterCheck()) return Optional.empty();
        
        AStarPathfinder aStarPathfinder = new AStarPathfinder(pathingRuleSet.withCounterCheck(false));
        PathfinderResult pathfinderResult = aStarPathfinder.resolvePath(target, start, strategy);
        
        if(pathfinderResult.getPathState() == PathState.FOUND)
            return Optional.of(pathfinderResult);
        
        return Optional.empty();
    }

}
