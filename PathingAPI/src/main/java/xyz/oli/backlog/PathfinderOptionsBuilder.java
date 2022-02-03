package xyz.oli.backlog;

import lombok.Builder;
import org.bukkit.Location;
import xyz.oli.pathing.PathfinderStrategy;

@Builder
public class PathfinderOptionsBuilder {

    // why is this protected?
    protected Location start;
    protected Location target;
    protected boolean asyncMode;
    protected PathfinderStrategy strategy;

    public PathfinderOptions build() {
        return new PathfinderOptions(this);
    }

}