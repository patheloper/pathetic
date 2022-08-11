package xyz.ollieee.api.pathing;

import lombok.NonNull;
import xyz.ollieee.api.pathing.result.task.PathingTask;
import xyz.ollieee.api.pathing.rules.PathingRuleSet;
import xyz.ollieee.api.wrapper.PathLocation;

public interface Pathfinder {

    /**
     * Tries to find a Path between the two {@link PathLocation}'s provided.
     *
     * @return {@link PathingTask} the the task object
     */
    @NonNull
    PathingTask findPath(@NonNull PathLocation start, @NonNull PathLocation target);

    /**
     * Tries to find a Path between the two {@link PathLocation}'s using the {@link PathingRuleSet} provided.
     *
     * @return {@link PathingTask} the the task object
     */
    @NonNull
    PathingTask findPath(@NonNull PathLocation start, @NonNull PathLocation target, PathingRuleSet ruleSet);

}
