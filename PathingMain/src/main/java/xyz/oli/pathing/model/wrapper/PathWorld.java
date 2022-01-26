package xyz.oli.pathing.model.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Objects;
import java.util.UUID;

public class PathWorld {
    
    private final UUID uuid;
    
    public PathWorld(UUID uuid) {
        this.uuid = uuid;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.uuid);
    }

    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PathWorld{");
        sb.append("uuid=").append(uuid);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathWorld pathWorld = (PathWorld) o;
        return pathWorld.uuid.equals(this.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
