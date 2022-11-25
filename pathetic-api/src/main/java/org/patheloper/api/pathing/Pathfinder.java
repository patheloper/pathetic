package org.patheloper.api.pathing;

import lombok.NonNull;
import org.patheloper.api.pathing.result.PathfinderResult;
import org.patheloper.api.wrapper.PathLocation;

import java.util.concurrent.CompletionStage;

/**
 * A Pathfinder is a class that can find a path between two locations while following a given set of rules.
 */
public interface Pathfinder {

    /**
     * Tries to find a Path between the two {@link PathLocation}'s provided.
     *
     * @return An {@link CompletionStage} that will contain a {@link PathfinderResult}.
     */
    @NonNull
    CompletionStage<PathfinderResult> findPath(@NonNull PathLocation start, @NonNull PathLocation target);
}
