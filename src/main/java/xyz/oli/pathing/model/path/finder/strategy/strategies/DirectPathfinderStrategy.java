package xyz.oli.pathing.model.path.finder.strategy.strategies;

import org.bukkit.Location;
import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;

public class DirectPathfinderStrategy extends PathfinderStrategy {

    @Override
    public boolean isValid(Location location, Location previous, Location previouser) {
        return location.getBlock().isEmpty();
    }

    @Override
    public boolean verifyEnd(Location location) {
        return location.getBlock().isEmpty();
    }

}
