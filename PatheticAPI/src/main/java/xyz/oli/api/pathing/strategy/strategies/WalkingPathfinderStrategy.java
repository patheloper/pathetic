package xyz.oli.api.pathing.strategy.strategies;

import lombok.NonNull;
import xyz.oli.api.pathing.strategy.PathfinderStrategy;
import xyz.oli.api.wrapper.PathBlock;

import java.util.Collections;
import java.util.List;

public class WalkingPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathBlock current, @NonNull PathBlock previous, @NonNull PathBlock previouser) {

        List<Integer> heights = List.of(current.getBlockY(), previous.getBlockY(), previouser.getBlockY());

        boolean currentIsValid = this.blockIsValid(current);
        boolean previousIsValid = this.blockIsValid(previous);
        boolean previouserIsValid = this.blockIsValid(previouser);

        int dy = Collections.max(heights) - Collections.min(heights);

        if (dy == 1) {
            // This means that two of the locations are valid, which allows for a jump of one block
            return (Collections.frequency(List.of(currentIsValid, previousIsValid, previouserIsValid), true) >= 2 || previouserIsValid) && current.isPassable();
        }else if (dy >= 2) {
            // Change in height of more than 1 means its impossible to traverse
            return false;
        }
        // Didn't change height
        return (Collections.frequency(List.of(currentIsValid, previousIsValid, previouserIsValid), true) >= 2 || previouserIsValid) && current.isPassable();
    }

    @Override
    public boolean endIsValid(@NonNull PathBlock block) {
        return this.blockIsValid(block);
    }

    private boolean blockIsValid(PathBlock block) {
        boolean passableValid = block.isPassable();
        boolean belowValid = !block.getPathLocation().clone().subtract(0, 1, 0).getBlock().isPassable();
        boolean aboveValid = block.getPathLocation().clone().add(0, 1, 0).getBlock().isPassable();

        return aboveValid && passableValid && belowValid;
    }
}
