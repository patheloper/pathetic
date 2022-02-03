package xyz.oli.backlog;

import lombok.Builder;
import org.bukkit.Location;
import xyz.oli.pathing.PathfinderStrategy;

@Builder
public class PathfinderOptionsBuilder {

    public Location start;
    public Location target;
    public boolean asyncMode;
    public PathfinderStrategy strategy;

    public PathfinderOptions build() {
        return new PathfinderOptions(this);
    }

}