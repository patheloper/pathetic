package xyz.oli.pathing.model.wrapper;

import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import xyz.oli.pathing.model.path.finder.strategy.chunks.SnapshotManager;

import java.util.Objects;

public class PathLocation implements Cloneable{

    private final PathWorld pathWorld;
    private double x, y, z;
    
    public PathLocation(PathWorld pathWorld, double x, double y, double z) {
        
        this.pathWorld = pathWorld;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PathWorld getPathWorld() {
        return this.pathWorld;
    }

    public double getX() {
        return this.x;
    }

    public int getBlockX() {
        return (int) Math.floor(this.x);
    }

    public double getY() {
        return this.y;
    }

    public int getBlockY() {
        return (int) Math.floor(this.y);
    }

    public double getZ() {
        return this.z;
    }

    public int getBlockZ() {
        return (int) Math.floor(this.z);
    }

    public PathLocation clone() {
        return new PathLocation(this.pathWorld, this.x, this.y, this.z);
    }

    public double distance(PathLocation otherLocation) {
        return Math.sqrt(NumberConversions.square(this.x - otherLocation.x) + NumberConversions.square(this.y - otherLocation.y) + NumberConversions.square(this.z - otherLocation.z));
    }

    public PathLocation add(final double x, final double y, final double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public PathLocation add(final Vector vector) {
        this.x += vector.getX();
        this.y += vector.getY();
        this.z += vector.getZ();
        return this;
    }

    public PathBlock getBlock() {
        return SnapshotManager.getBlock(this);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PathLocation{");
        sb.append("pathWorld=").append(pathWorld);
        sb.append(", x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", z=").append(z);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathLocation other = (PathLocation) o;
        return Double.compare(other.x, x) == 0 && Double.compare(other.y, y) == 0 && Double.compare(other.z, z) == 0 && pathWorld.equals(other.pathWorld);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathWorld, x, y, z);
    }
}
