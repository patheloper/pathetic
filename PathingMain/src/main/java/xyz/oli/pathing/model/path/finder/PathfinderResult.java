package xyz.oli.pathing.model.path.finder;

import lombok.AllArgsConstructor;
import lombok.Value;
import xyz.oli.pathing.model.path.Path;

@Value
@AllArgsConstructor
public class PathfinderResult {
    
    PathfinderSuccess pathfinderSuccess;
    Path path;

    public boolean successful() {
        return pathfinderSuccess == PathfinderSuccess.FOUND;
    }

    enum PathfinderSuccess {
        /**
         * The Path was successfully found for a given strategy
         */
        FOUND,
        /**
         * The Path wasn't found, either the start/finish were invalid, it reached its max search depth, or it couldn't find more locations
         */
        FAILED
    }
}
