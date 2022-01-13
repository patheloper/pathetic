package xyz.oli.pathing.api;

import org.bukkit.Location;

import java.util.LinkedList;

public class Path {

    private final Location start;
    private final Location target;
    private final LinkedList<Location> path;

    public Path(Location start, Location target, LinkedList<Location> path) {
        this.start = start;
        this.target = target;
        this.path = path;
    }

    public LinkedList<Location> getPath() {
        return this.path;
    }

    public Location getStart() {
        return this.start;
    }

    public Location getTarget() {
        return this.target;
    }
}
