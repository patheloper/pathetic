package xyz.oli.pathing.model.finder.strategies;

import lombok.NonNull;
import xyz.oli.api.pathing.strategy.PathfinderStrategy;
import xyz.oli.api.wrapper.PathBlock;

import java.util.Collections;
import java.util.List;

public class WalkingPathfinderStrategy implements PathfinderStrategy {

    @Override
    public boolean isValid(@NonNull PathBlock current, @NonNull PathBlock previous, @NonNull PathBlock previouser) {
        return current.isEmpty() && current.getPathLocation().clone().add(0,1,0).getBlock().isEmpty() && this.validateJump(current, previous, previouser);
    }

    @Override
    public boolean endIsValid(@NonNull PathBlock block) {
        return block.isPassable() && block.getPathLocation().clone().add(0,1,0).getBlock().isPassable() && !block.getPathLocation().clone().add(0,-1,0).getBlock().isPassable();
    }

    private boolean validateJump(PathBlock current, PathBlock previous, PathBlock previouser) {

        List<Integer> heights = List.of(current.getBlockY(), previous.getBlockY(), previouser.getBlockY());

        if (Collections.max(heights) - Collections.min(heights) < 0)
            return false;

        if (current.getBlockY() == previous.getBlockY()) {
            if (current.getPathLocation().clone().add(0,-1,0).getBlock().isPassable() && current.getPathLocation().clone().add(0, -2, 0).getBlock().isPassable()) {
                return false;
            }
        }

        return !current.getPathLocation().clone().add(0, -1, 0).getBlock().isPassable() || !previous.getPathLocation().clone().add(0, -1, 0).getBlock().isPassable();
    }
}
