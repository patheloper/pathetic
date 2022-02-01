package xyz.oli.pathing.model.path;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.bukkit.Location;

import java.util.LinkedHashSet;

@Value
@AllArgsConstructor
public class Path {

    Location start;
    Location end;
    LinkedHashSet<Location> path;
}
