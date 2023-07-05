package org.patheloper.model.pathfinder;

import org.bukkit.plugin.java.JavaPlugin;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.patheloper.api.pathing.Pathfinder;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.pathing.rules.PathingRuleSet;
import org.patheloper.api.wrapper.PathEnvironment;
import org.patheloper.api.wrapper.PathPosition;
import org.patheloper.mapping.PatheticMapper;

import java.util.concurrent.CompletionStage;

import static org.junit.Assert.*;

public class PathfinderTest {

    @Before
    void setUp() {
        PatheticMapper.initialize(Mockito.mock(JavaPlugin.class));
    }

    @Test
    void doesFindPath() {

        /* Given */
        PathEnvironment environment = new PathEnvironment(null, "test", 0, 256);
        PathPosition start = new PathPosition(environment, 0, 0, 0);
        PathPosition target = new PathPosition(environment, 10, 0, 10);

        Pathfinder sut = PatheticMapper.newPathfinder(PathingRuleSet.createRuleSet());

        /* When */
        CompletionStage<PathfinderResult> path = sut.findPath(start, target);
        PathfinderResult result = path.toCompletableFuture().join();

        /* Then */
        assertTrue(result.successful());
    }
}
