package xyz.ollieee.api.wrapper;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import xyz.ollieee.api.PatheticAPI;

@ToString
@EqualsAndHashCode
public class PathLocation implements Cloneable {

    private final PathWorld pathWorld;
    private double x, y, z;
    
    public PathLocation(@NonNull PathWorld pathWorld, double x, double y, double z) {
        
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

    @Override
    public PathLocation clone() {
        return new PathLocation(this.pathWorld, this.x, this.y, this.z);
    }

    private double sqrt(double input) {
        double sqrt = Double.longBitsToDouble(((Double.doubleToLongBits(input)-(1L<<52))>>1) + (1L <<61));
        double better = (sqrt + input/sqrt)/2.0;
        return (better + input/better)/2.0;
    }

    private double square(double value){
        return value * value;
    }

    public double distance(PathLocation otherLocation) {
        return sqrt(this.square(this.x - otherLocation.x) + this.square((this.y - otherLocation.y) * 1.1) + this.square(this.z - otherLocation.z));
    }

    public PathLocation add(final double x, final double y, final double z) {
        
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public PathLocation add(final PathVector vector) {
        add(vector.getX(), vector.getY(), vector.getZ());
        return this;
    }
    
    public PathLocation subtract(final double x, final double y, final double z) {
        
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
    
    public PathLocation subtract(final PathVector vector) {
        subtract(vector.getX(), vector.getY(), vector.getZ());
        return this;
    }

    public PathVector toVector() {
        return new PathVector(this.x, this.y, this.z);
    }

    public PathBlock getBlock() {
        return PatheticAPI.getSnapshotManager().getBlock(this);
    }

}
