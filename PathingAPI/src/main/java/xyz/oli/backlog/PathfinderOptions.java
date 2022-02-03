package xyz.oli.backlog;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.Location;
import xyz.oli.pathing.PathfinderStrategy;

@Value
@AllArgsConstructor
public class PathfinderOptions {

    boolean asyncMode;
    Location start;
    Location target;
    PathfinderStrategy strategy;

    public PathfinderOptions(@NonNull PathfinderOptionsBuilder builder) {
        this.start = builder.start;
        this.target = builder.target;
        this.asyncMode = builder.asyncMode;
        this.strategy = builder.strategy;
    }
}
