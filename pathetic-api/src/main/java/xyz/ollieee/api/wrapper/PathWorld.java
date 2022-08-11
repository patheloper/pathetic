package xyz.ollieee.api.wrapper;

import java.util.Objects;
import java.util.UUID;

public final class PathWorld {

    private final UUID uuid;
    private final String name;
    private final Integer minHeight;
    private final Integer maxHeight;

    public PathWorld(UUID uuid, String name, Integer minHeight, Integer maxHeight) {
        this.uuid = uuid;
        this.name = name;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public Integer getMinHeight() {
        return this.minHeight;
    }

    public Integer getMaxHeight() {
        return this.maxHeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathWorld pathWorld = (PathWorld) o;
        return Objects.equals(uuid, pathWorld.uuid) && Objects.equals(name, pathWorld.name) && Objects.equals(minHeight, pathWorld.minHeight) && Objects.equals(maxHeight, pathWorld.maxHeight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, minHeight, maxHeight);
    }

    public String toString() {
        return "PathWorld(uuid=" + this.getUuid() + ", name=" + this.getName() + ", minHeight=" + this.getMinHeight() + ", maxHeight=" + this.getMaxHeight() + ")";
    }
}
