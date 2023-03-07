package org.patheloper.model.pathing;

import com.google.common.collect.Iterables;
import org.patheloper.api.pathing.result.Path;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;
import org.patheloper.util.WatchdogUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
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

        // Construct the path
        Optional<PathfinderResult> result = constructPath(new LinkedList<>(), nodeQueue, examinedPositions, strategy, 0);
        return result.orElseGet(() -> finishPathing(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET))));
    }

    private Optional<PathfinderResult> constructPath(LinkedList<Path> triedPaths, PriorityQueue<Node> nodeQueue, Set<PathPosition> examinedPositions, PathfinderStrategy strategy, int recursionDepth) {

        // This is the current depth of the search and the last node
        int depth = 1;
        Node lastEverFound = null;

        System.out.println("Starting iteration");
        while (!nodeQueue.isEmpty() && depth <= pathingRuleSet.getMaxIterations()) {

            // Every 500 iterations, tick the watchdog so that a watchdog timeout doesn't occur
            if (depth % 500 == 0) WatchdogUtil.tickWatchdog();

            // Get the next node from the queue
            Node currentNode = nodeQueue.poll();
            if (currentNode == null)
                throw new IllegalStateException("Something just exploded");

            if(lastEverFound == null || currentNode.heuristic() < lastEverFound.heuristic())
                lastEverFound = currentNode;

            // Check to see if we have reached the length limit
            if (pathingRuleSet.getMaxLength() > 0 && PathingHelper.getProgress(lastEverFound) >= pathingRuleSet.getMaxLength())
                return Optional.of(finishPathing(new PathfinderResultImpl(PathState.FOUND, PathingHelper.fetchRetracedPath(lastEverFound))));

            // This means that the current node is the target, so we can stop here
            if (currentNode.hasReachedEnd())
                return Optional.of(finishPathing(new PathfinderResultImpl(PathState.FOUND, PathingHelper.fetchRetracedPath(lastEverFound))));

            PathingHelper.evaluateNewNodes(nodeQueue, examinedPositions, currentNode, offset, strategy, snapshotManager);
            depth++;
        }

        System.out.println("Iteration failed");
        System.out.println("Recursion depth: " + recursionDepth);

        // If it's allowed to find a path recursively, we try to find a path recursively
        if (pathingRuleSet.isAllowingRecursiveFinding() && lastEverFound != null) {

            // Add the last found path to the list of already tried paths
            triedPaths.add(PathingHelper.fetchRetracedPath(lastEverFound));
            Optional<PathfinderResult> result = recursivePathConstruction(triedPaths, strategy, recursionDepth);

            if (result.isPresent())
                return result;
            else if(pathingRuleSet.isAllowingFallback() && !triedPaths.isEmpty())
                return Optional.of(finishPathing(new PathfinderResultImpl(PathState.FALLBACK, triedPaths.getFirst())));
        }

        if (pathingRuleSet.isAllowingFallback() && lastEverFound != null)
            return Optional.of(finishPathing(new PathfinderResultImpl(PathState.FALLBACK, PathingHelper.fetchRetracedPath(lastEverFound))));

        return Optional.empty();
    }

    private Optional<PathfinderResult> recursivePathConstruction(LinkedList<Path> alreadyTriedPaths, PathfinderStrategy strategy, int recursionDepth) {

        if(recursionDepth > pathingRuleSet.getMaxRecursionDepth())
            return Optional.empty();

        // Since we weren't successful in the previous attempt, we use the previous path to not end up in the same situation again
        Path firstPath = alreadyTriedPaths.getFirst();
        PathPosition start = firstPath.getStart();
        PathPosition target = firstPath.getEnd();

        // Create the initial node
        Node startNode = new Node(start.floor(), start.floor(), target.floor(), 0);

        // Create the open and closed sets
        PriorityQueue<Node> nodeQueue = new PriorityQueue<>(Collections.singleton(startNode));
        Set<PathPosition> examinedPositions = new HashSet<>();

        // Adding the "found" positions to the examined positions except of the first 5 positions (to avoid the start positions)
        // of all the already tried paths and fast return if no valid path can be found anymore
        for(Path path : alreadyTriedPaths) {
            ArrayList<PathPosition> positions = new ArrayList<>(Arrays.asList(Iterables.toArray(path.getPositions(), PathPosition.class)));
            if(!positions.isEmpty() && positions.size() > 5)
                examinedPositions.addAll(positions.subList(5, positions.size()));
            else
                return Optional.empty();
        }

        return constructPath(alreadyTriedPaths, nodeQueue, examinedPositions, strategy, recursionDepth + 1);
    }

}
