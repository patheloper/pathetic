package xyz.oli.pathing.api;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class Path {

    private final Location start;
    private final Location target;
    private final LinkedHashSet<Location> path;

    public Path(Location start, Location target, LinkedHashSet<Location> path) {
        this.start = start;
        this.target = target;
        this.path = path;
    }

    public LinkedHashSet<Location> getPath() {
        return this.path;
    }

    public Location getStart() {
        return this.start;
    }

    public Location getTarget() {
        return this.target;
    }
}
