package xyz.ollieee.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

import java.util.UUID;

@Value
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PathWorld {

    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    UUID uuid;

    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    Integer minHeight;

    @Getter
    @EqualsAndHashCode.Include
    @ToString.Include
    Integer maxHeight;
    
    @Getter
    @EqualsAndHashCode.Exclude
    @ToString.Include
    String name;
    
    public PathWorld(UUID uuid, String name, Integer minHeight, Integer maxHeight) {
        
        this.uuid = uuid;
        this.name = name;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }
}
