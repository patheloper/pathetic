package org.patheloper.model.pathing.pathfinder;

import org.patheloper.api.annotation.Experimental;
import org.patheloper.api.pathing.result.PathState;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.pathing.strategy.PathfinderStrategy;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.model.pathing.result.PathImpl;
import org.patheloper.model.pathing.result.PathfinderResultImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A pathfinder that uses the A* algorithm.
 * <p>
 * This pathfinder uses {@link AStarPathfinder} bidirectional,
 *      meaning that it will search from the start to the midpoint and from the target to the midpoint.
 * <p>
 *
 * @experimental This pathfinder is experimental and may not work as expected.
 */
@Experimental
public class BidirectionalAStarPathfinder extends AbstractPathfinder {
    
    private final AStarPathfinder aStarPathfinder;
    
    public BidirectionalAStarPathfinder(PathingRuleSet ruleSet) {
        super(ruleSet);

        this.aStarPathfinder = new AStarPathfinder(ruleSet.withAsync(false)
                .withMaxIterations(ruleSet.getMaxIterations() / 2));
    }
    
    @Override
    protected PathfinderResult findPath(PathPosition start, PathPosition target, PathfinderStrategy strategy) {
        
        PathPosition midPoint = start.midPoint(target);
        
        PathfinderResult firstHalf = aStarPathfinder.findPath(start, midPoint, strategy);
        PathfinderResult secondHalf = aStarPathfinder.findPath(firstHalf.getPath().getEnd(), target, strategy);
        
        if(!firstHalf.successful() || !secondHalf.successful())
            return finishPathing(new PathfinderResultImpl(PathState.FAILED, new PathImpl(start, target, EMPTY_LINKED_HASHSET)));
    
        return finishPathing(new PathfinderResultImpl(PathState.FOUND, new PathImpl(start, target, mergePaths(firstHalf.getPath().getPositions(), secondHalf.getPath().getPositions()))));
    }

    private Iterable<PathPosition> mergePaths(Iterable<PathPosition> firstHalf, Iterable<PathPosition> secondHalf) {

        Set<PathPosition> merged = new HashSet<>();
        firstHalf.forEach(merged::add);

        Iterable<PathPosition> pathPositions = reverseIterable(secondHalf);
        pathPositions.forEach(merged::add);

        return merged;
    }
    
    private Iterable<PathPosition> reverseIterable(Iterable<PathPosition> iterable) {

        List<PathPosition> list = new ArrayList<>();
        iterable.forEach(list::add);

        Collections.reverse(list);
        return list;
    }
}
