package xyz.oli.pathing.model.path;

import lombok.AllArgsConstructor;
import org.bukkit.Location;

import java.util.LinkedHashSet;

@AllArgsConstructor
public class Path implements xyz.oli.pathing.Path {

    Location start;
    Location end;
    LinkedHashSet<Location> path;

    @Override
    public LinkedHashSet<Location> getPath() {
        return this.path;
    }

    @Override
    public Location getStart() {
        return this.start;
    }

    @Override
    public Location getEnd() {
        return this.end;
    }
}
