package xyz.oli.pathing.model.wrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Objects;
import java.util.UUID;

@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PathWorld {

     // TODO world name 

    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID uuid;
    
    public PathWorld(UUID uuid) {
        this.uuid = uuid;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.uuid);
    } // we have a converter for this
}
