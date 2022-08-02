package xyz.ollieee.api.pathing;

import lombok.NonNull;
import xyz.ollieee.api.pathing.result.PathfinderResult;
import xyz.ollieee.api.pathing.result.task.PathingTask;
import xyz.ollieee.api.pathing.rules.PathingRuleSetBuilder;

public interface Pathfinder {

    /**
     * Tries to find a Path using the {@link PathingRuleSetBuilder.PathingRuleSet} provided.
     *
     * @return {@link PathingTask<PathfinderResult>} the the task object
     */
    @NonNull
    PathingTask<PathfinderResult> findPath(@NonNull PathingRuleSetBuilder.PathingRuleSet ruleSet);

    /**
     * @return {@link PathingTask<PathfinderResult>} the task object
     * @see #findPath(PathingRuleSetBuilder.PathingRuleSet)  - but async
     */
    @NonNull
    PathingTask<PathfinderResult> findPathAsync(@NonNull PathingRuleSetBuilder.PathingRuleSet ruleSet);

}
