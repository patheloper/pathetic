package org.patheloper.model.pathfinder;

import org.junit.Test;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.wrapper.PathEnvironment;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.model.pathing.pathfinder.AStarPathfinder;

import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertTrue;

public class PathfinderTest {

    @Test
    void doesAStarPathfinderFindPath() {

        /* Given */
        PathEnvironment environment = new PathEnvironment(null, "test", 0, 256);
        PathPosition start = new PathPosition(environment, 0, 0, 0);
        PathPosition target = new PathPosition(environment, 10, 0, 10);

        Pathfinder sut = new AStarPathfinder(PathingRuleSet.createRuleSet());

        /* When */
        CompletionStage<PathfinderResult> path = sut.findPath(start, target);
        PathfinderResult result = path.toCompletableFuture().join();

        /* Then */
        assertTrue(result.successful());
    }
}
