package xyz.oli.pathing.model.path;

import org.bukkit.Location;

import java.util.LinkedHashSet;

public class Path {

    private final Location start;
    private final Location end;
    private final LinkedHashSet<Location> path;

    public Path(Location start, Location end, LinkedHashSet<Location> path) {
        this.start = start;
        this.end = end;
        this.path = path;
    }

    public LinkedHashSet<Location> getPath() {
        return this.path;
    }

    public Location getStart() {
        return this.start;
    }

    public Location getEnd() {
        return this.end;
    }
}
