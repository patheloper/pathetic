package xyz.ollieee.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.ollieee.api.pathing.strategy.PathfinderStrategy;
import xyz.ollieee.api.wrapper.PathBlock;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A {@link PathfinderStrategy} useful for walking. Not perfect and a WIP
 * @deprecated WIP
 */
public class WalkingPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathBlock current, PathBlock previous, PathBlock previouser) {

        if (previous == null && previouser == null) return blockIsValid(current);

        List<Integer> heights = Arrays.asList(current.getBlockY(), previous.getBlockY(), previouser.getBlockY());

        boolean currentIsValid = this.blockIsValid(current);
        boolean previousIsValid = this.blockIsValid(previous);
        boolean previouserIsValid = this.blockIsValid(previouser);

        int dy = Collections.max(heights) - Collections.min(heights);

        if (dy == 1) {
            // This means that two of the locations are valid, which allows for a jump of one block
            return (previouserIsValid || Collections.frequency(Arrays.asList(currentIsValid, previousIsValid, previouserIsValid), true) >= 2) && current.isPassable();
        }else if (dy >= 2) {
            // Change in height of more than 1 means its impossible to traverse
            return false;
        }
        // Didn't change height
        return (previouserIsValid || Collections.frequency(Arrays.asList(currentIsValid, previousIsValid, previouserIsValid), true) >= 2) && current.isPassable();
    }

    private boolean blockIsValid(PathBlock block) {
        boolean passableValid = block.isPassable();
        boolean belowValid = !block.getPathLocation().clone().subtract(0, 1, 0).getBlock().isPassable();
        boolean aboveValid = block.getPathLocation().clone().add(0, 1, 0).getBlock().isPassable();

        return aboveValid && passableValid && belowValid;
    }
}
