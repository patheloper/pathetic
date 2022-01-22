package xyz.oli.pathing.model.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;

public class PathWorld {
    
    private final UUID uuid;
    
    public PathWorld(UUID uuid) {
        this.uuid = uuid;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.uuid);
    }
}
