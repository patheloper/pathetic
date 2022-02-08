package xyz.oli.pathing.model.pathing;

import lombok.AllArgsConstructor;
import org.bukkit.Location;
import xyz.oli.api.pathing.result.Path;

import java.util.LinkedHashSet;

@AllArgsConstructor
public class PathImpl implements Path {

    Location start;
    Location end;
    LinkedHashSet<Location> locations;

    @Override
    public LinkedHashSet<Location> getLocations() {
        return this.locations;
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
