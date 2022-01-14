package xyz.oli.pathing.api.strategy;

import org.bukkit.Location;

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
