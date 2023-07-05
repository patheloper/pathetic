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
    protected PathfinderResult findPath(PathPosition start, PathPosition target, PathfinderStrategy strategy) {

        // Create the initial node
        Node startNode = new Node(start.floor(), start.floor(), target.floor(), 0);

        // Create the open and closed sets
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));
        Set<PathPosition> examinedPositions = new HashSet<>();

        // This is the current depth of the search and the last node
        int depth = 1;
        Node fallbackNode = null;

        while (!nodeQueue.isEmpty() && depth <= pathingRuleSet.getMaxIterations()) {

            // Every 500 iterations, tick the watchdog so that a watchdog timeout doesn't occur
            if (depth % 500 == 0) WatchdogUtil.tickWatchdog();

            // Get the next node from the queue
            Node currentNode = nodeQueue.poll();
            if (currentNode == null)
                throw ErrorLogger.logFatalError("A node was null when it shouldn't have been");

            fallbackNode = currentNode;

            // Check to see if we have reached the length limit
            if (pathingRuleSet.getMaxLength() > 0)
                return finishPathing(new PathfinderResultImpl(PathState.LENGTH_LIMITED, NodeUtil.fetchRetracedPath(currentNode)));

            // This means that the current node is the target, so we can stop here
            if (currentNode.isAtTarget())
                return finishPathing(new PathfinderResultImpl(PathState.FOUND, NodeUtil.fetchRetracedPath(currentNode)));

            NodeUtil.evaluateNewNodes(nodeQueue, examinedPositions, currentNode, offset, strategy, snapshotManager);
            depth++;
        }
        
        if(pathingRuleSet.isCounterCheck()) {
            Optional<PathfinderResult> counterCheck = counterCheck(start, target, strategy);
            if(counterCheck.isPresent())
                return counterCheck.get();
        }

        if (pathingRuleSet.isAllowingFallback() && fallbackNode != null)
            return finishPathing(new PathfinderResultImpl(PathState.FALLBACK, NodeUtil.fetchRetracedPath(fallbackNode)));

        return finishPathing(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));
    }
    
    private Optional<PathfinderResult> counterCheck(PathPosition start, PathPosition target, PathfinderStrategy strategy) {
        
        AStarPathfinder aStarPathfinder = new AStarPathfinder(pathingRuleSet.withCounterCheck(false));
        PathfinderResult pathfinderResult = aStarPathfinder.findPath(target, start, strategy);
        
        if(pathfinderResult.getPathState() == PathState.FOUND)
            return Optional.of(pathfinderResult);
        
        return Optional.empty();
    }

}
