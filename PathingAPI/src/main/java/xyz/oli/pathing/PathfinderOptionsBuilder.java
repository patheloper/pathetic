package xyz.oli.pathing;

import org.bukkit.Location;

import xyz.oli.pathing.model.path.finder.strategy.PathfinderStrategy;

public class PathfinderOptionsBuilder {

    protected Location start;
    protected Location target;
    protected boolean asyncMode;
    protected PathfinderStrategy strategy;

    public PathfinderOptionsBuilder start(Location location) {
        this.start = location;
        return this;
    }

    public PathfinderOptionsBuilder target(Location location) {
        this.target = location;
        return this;
    }

    public PathfinderOptionsBuilder asyncMode(boolean value) {
        this.asyncMode = value;
        return this;
    }

    public PathfinderOptionsBuilder strategy(PathfinderStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public PathfinderOptions build() {
        return new PathfinderOptions(this);
    }

}