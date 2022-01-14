package xyz.oli.pathing.api.strategy.strategies;

import org.bukkit.Location;
import xyz.oli.pathing.api.strategy.PathfinderStrategy;

import java.util.Collections;
import java.util.List;

public class WalkingPathfinderStrategy extends PathfinderStrategy {

    private boolean validateJump(Location location, Location previous, Location previouser) {

        List<Integer> heights = List.of(location.getBlockY(), previous.getBlockY(), previouser.getBlockY());

        if (Collections.max(heights) - Collections.min(heights) < 0)
            return false;

        if (location.getBlockY() == previous.getBlockY()) {
            if (location.clone().add(0,-1,0).getBlock().isPassable() && location.clone().add(0, -2, 0).getBlock().isPassable()) {
                return false;
            }
        }
    
        return !location.clone().add(0, -1, 0).getBlock().isPassable() || !previous.clone().add(0, -1, 0).getBlock().isPassable();
    }

    @Override
    public boolean isValid(Location location, Location previous, Location previouser) {
        return location.getBlock().isEmpty() && location.clone().add(0,1,0).getBlock().isEmpty() && this.validateJump(location, previous, previouser);
    }

    @Override
    public boolean verifyEnd(Location location) {
        return location.getBlock().isPassable() && location.clone().add(0,1,0).getBlock().isPassable() && !location.clone().add(0,-1,0).getBlock().isPassable();
    }
}
