package xyz.oli.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PathWorld {

    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    private final UUID uuid;
    
    @Getter
    @EqualsAndHashCode.Exclude
    @ToString.Include
    private final String name;
    
    public PathWorld(UUID uuid, String name) {
        
        this.uuid = uuid;
        this.name = name;
    }
}
