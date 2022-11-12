package org.patheloper.api.pathing;

import lombok.NonNull;
import org.patheloper.api.pathing.result.task.PathingTask;
import org.patheloper.api.wrapper.PathLocation;

public interface Pathfinder {

    /**
     * Tries to find a Path between the two {@link PathLocation}'s provided.
     *
     * @return {@link PathingTask} the the task object
     */
    @NonNull
    PathingTask findPath(@NonNull PathLocation start, @NonNull PathLocation target);
}
