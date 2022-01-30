package xyz.oli.pathing.model.path.finder.strategy.strategies;

import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;
import xyz.oli.pathing.model.wrapper.PathBlock;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

public class WalkingPathfinderStrategy extends PathfinderStrategy {

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

    @Override
    public boolean isValid(@NotNull PathBlock current, @NotNull PathBlock previous, @NotNull PathBlock previouser) {
        return current.isEmpty() && current.getPathLocation().clone().add(0,1,0).getBlock().isEmpty() && this.validateJump(current, previous, previouser);
    }

    @Override
    public boolean verifyEnd(@NotNull PathBlock block) {
        return block.isPassable() && block.getPathLocation().clone().add(0,1,0).getBlock().isPassable() && !block.getPathLocation().clone().add(0,-1,0).getBlock().isPassable();
    }
}
